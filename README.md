# CS Manager API

Spring Boot 4.1 / Java 21, Spring Security OAuth2 Resource Server, PostgreSQL e Spring AI 2.0.1. O Keycloak mantém identidades e senhas; a aplicação guarda somente jogadores e times. As antigas listas em memória foram substituídas por consultas SQL parametrizadas via services/repositories.

## Iniciar com Docker

Mantenha os checkouts como diretórios irmãos `cs-player-api` e `cs-player-front`. É necessário Docker com Compose v2.

```sh
cd cs-player-api
cp .env.example .env
# Edite .env: preencha DB_PASSWORD, KEYCLOAK_DB_PASSWORD,
# KEYCLOAK_ADMIN_USER e KEYCLOAK_ADMIN_PASSWORD com valores exclusivos.
docker compose up --build -d
docker compose ps
```

Neste ambiente de nuvem já provisionado, a validação usa `/workspace/cloud-setup/docker.env` (permissão 0600), fora dos repositórios. Para reutilizar os volumes existentes, acrescente `--env-file /workspace/cloud-setup/docker.env` ao comando `docker compose`, em vez de gerar novas senhas. As credenciais das contas temporárias de teste estão em `/workspace/cloud-setup/test-accounts.json`, também com permissão 0600; não as publique nem as versione. O console permite criar suas contas definitivas.

Não versionar `.env`. Não há conta, senha administrativa ou usuário de aplicação embutido no realm. Os bancos da aplicação e do Keycloak usam containers e volumes separados. `docker compose down` preserva os volumes. A importação do realm ocorre na primeira inicialização; editar o JSON não sobrescreve um realm já existente. Atualize realms existentes pelo console/administração controlada, sem apagar volumes com dados importantes.

| Serviço local | Endereço / porta |
|---|---|
| Angular | `http://localhost:4200` |
| API | `http://localhost:8081/api` |
| OIDC público | `http://localhost:8080/realms/cs-manager` |
| Console administrativo, somente loopback | `http://localhost:8082/admin/` |
| PostgreSQL da aplicação, somente loopback | `localhost:5432`, banco/usuário `cs_manager` |

A entrada OIDC pública expõe apenas `/realms/cs-manager/` e `/resources/`; `/admin/` e o realm master não são encaminhados. O console direto está vinculado a `127.0.0.1`. Não publique a porta administrativa na Internet. O realm bloqueia autorregistro, implicit flow e password grant nos clients da aplicação. O client público exige PKCE S256 e adiciona `cs-manager-api` ao audience do access token.

Os healthchecks verificam PostgreSQL, Keycloak, API e frontend. `GET /actuator/health` é público e expõe somente saúde básica. Todas as outras rotas não explicitamente autorizadas são negadas.

## Criar e administrar usuários

1. Entre no console local com as credenciais administrativas definidas no seu `.env` e selecione `cs-manager`.
2. Em **Users → Add user**, crie a conta e habilite **Enabled**. Não atribua permissões administrativas do Keycloak aos usuários da aplicação.
3. Em **Credentials**, defina uma senha inicial com **Temporary = On**. O primeiro login exigirá alteração. Também é possível selecionar **Required user actions → Update Password**; configure SMTP para ações por email, como recuperação de senha.
4. Em **Role mapping → Assign role → Filter by realm roles**, atribua `USER` ou `ADMIN`.
5. Para desabilitar uma conta, desligue **Enabled** e encerre suas sessões em **Sessions**. Tokens JWT já emitidos continuam válidos até expirar (5 minutos, com tolerância de relógio do validador); o próximo refresh/login será recusado. Para revogação imediata, uma política adicional de revogação/introspecção é necessária.

Não há endpoint da aplicação para atribuir roles. O Angular não recebe client secret nem credenciais administrativas.

## URLs, variáveis e execução fora do Docker

`compose.yaml` é para desenvolvimento local. O navegador usa o issuer público `http://localhost:8080/realms/cs-manager`; a API dentro do Docker consulta chaves por `http://keycloak:8080/.../certs`, validando o issuer público sem substituí-lo pelo hostname interno.

Para outro endereço, ajuste `KEYCLOAK_PUBLIC_URL`, `KEYCLOAK_ADMIN_URL`, `FRONTEND_URL`, `API_PUBLIC_URL` e também **Valid redirect URIs**, **Valid post logout redirect URIs** e **Web origins** do client frontend no Keycloak. Restrinja às origens reais, sem `*` global. O JSON importado aceita somente `http://localhost:4200/*`. Alterar apenas as variáveis do Compose não muda esse registro.

Para desenvolvimento sem containers da aplicação:

```sh
docker compose up -d app-db auth
# Exporte DB_PASSWORD sem imprimir seu valor; é o mesmo usado pelo app-db.
export SPRING_PROFILES_ACTIVE=dev
bash mvnw verify
bash mvnw spring-boot:run
# Em cs-player-front: npm ci && npm start
```

`DB_URL` (padrão `jdbc:postgresql://localhost:5432/cs_manager`), `DB_USER`, `DB_PASSWORD`, `KEYCLOAK_ISSUER`, `KEYCLOAK_JWKS` e `CORS_ORIGINS` configuram o backend. Os esquemas são versionados pelo Flyway. O profile `dev` adiciona os 20 jogadores demonstrativos existentes apenas uma vez, em uma migração separada. Não ative `dev` em produção; o schema de produção começa vazio.

Em produção, use HTTPS para aplicação, API e Keycloak, `kc.sh start` com configuração de produção, proxies confiáveis, segredos gerenciados e bancos persistentes com backup. Não reutilize `start-dev` nem exponha portas internas. O limitador do chat é por subject e por instância; em múltiplas réplicas, aplique também um limite compartilhado no gateway/Redis.

## Autorização e API

| Operação | USER | ADMIN |
|---|---|---|
| GET `/api/times`, `/api/times/{id}` | Sim | Sim |
| GET `/api/jogadores`, `/api/jogadores/{id}` | Sim | Sim |
| GET `/api/enums` | Sim | Sim |
| POST `/api/times`, `/api/jogadores` | Não | Sim |
| PUT e DELETE `/api/times/{id}`, `/api/jogadores/{id}` | Não | Sim |
| POST `/api/chat/mensagem` (somente consultas) | Sim | Sim |

A listagem de jogadores aceita `pais`, `timeId` e `funcao`. Cadastro/edição usam `nickname`, `nomeCompleto`, `idade`, `pais`, `timeId`, `funcao`, `armaFavorita` e `rating`. Times usam `{ "nome": "FURIA" }`. As respostas de jogadores incluem `time: { id, nome }`. Um time vinculado a jogadores não pode ser excluído (409). Validações retornam 400; ausência de registro retorna 404.

Requisições sem autenticação/token válido recebem 401; role insuficiente recebe 403. A assinatura RS256 é verificada com JWKS, junto de issuer, audience, expiração e not-before. As sessões HTTP são stateless; a autenticação ocorre exclusivamente pelo header Bearer, sem cookie de sessão da API. CORS permite somente origens configuradas.

## CS Manager AI

Defina, somente no backend:

| Variável | Padrão / finalidade |
|---|---|
| `AI_ENABLED` | `false`; use `true` após configurar o provedor |
| `AI_API_KEY` | Chave do provedor; nunca inclua no Angular ou no Git |
| `AI_BASE_URL` | `https://api.openai.com/v1`; endpoint compatível com OpenAI |
| `AI_MODEL` | `gpt-4.1-mini` |
| `AI_TIMEOUT` | `20s` por chamada; sem retries automáticos |
| `AI_MAX_TOKENS` | `1000` por resposta do modelo |
| `AI_MAX_RECORDS` | `50`; limite máximo efetivo de 100 por ferramenta |
| `AI_REQUESTS_PER_MINUTE` | `10` por usuário e instância |

Após alterar variáveis, recrie a API com `docker compose up -d api`. Nenhuma chamada paga é feita quando `AI_ENABLED=false`; o chat responde 503 com mensagem amigável. A chave não é enviada ao navegador.

```http
POST /api/chat/mensagem
Authorization: Bearer <access_token>
Content-Type: application/json

{"mensagem":"Liste os jogadores da FURIA"}
```

Resposta: `resposta` em português e `resultados`, uma lista de `{consulta, dados, limiteRegistros}`. O modelo interpreta a pergunta e seleciona ferramentas do Spring AI. O servidor executa somente funções de leitura registradas, reutilizando os services e o PostgreSQL. O texto livre produzido pelo modelo não é exibido como fato: a resposta e os dados visíveis são montados a partir dos resultados das ferramentas. Isso evita apresentar times, ratings ou jogadores inventados. O histórico visual existe apenas em memória no navegador; cada requisição é independente e não envia conversas anteriores ao provedor.

Há ferramentas para times, jogador por nickname, país, função, arma, rating mínimo, ranking, média e quantidade por time, contagem por país e comparação entre times. `NATUS_VINCERE` é aceito como alias de `NAVI`. Listas são limitadas e identificadas como tal; contagens e médias são agregações exatas no PostgreSQL. Não há ferramenta de SQL arbitrário, escrita, usuários, credenciais ou tokens. Apenas dados públicos dos registros de CS2 são fornecidos ao modelo.

As mensagens têm até 2000 caracteres. Há no máximo seis rodadas do modelo e oito execuções de ferramentas por solicitação. 429 indica limite de requisições; 503 indica indisponibilidade/desativação do provedor. Lista vazia e time inexistente são resultados de consulta, não falhas do provedor. Perguntas sem uma consulta permitida recebem uma solicitação de esclarecimento.

## Testes

```sh
bash mvnw verify
# Frontend, no outro checkout:
npm test -- --watch=false
npm run build
```

Os testes de backend usam H2 em modo PostgreSQL para migrações/CRUD e um servidor JWKS local com JWTs assinados. Cobrem 401/403, issuer/audience/expiração/assinatura, CORS, ADMIN/USER, CRUD, vínculo entre entidades, ranking, média, ausências, limites e indisponibilidade. O provedor de IA é mockado, incluindo um teste HTTP do SDK e tool calling completo, sem chamadas pagas. Valide também com PostgreSQL real via Compose antes de implantar.

Roteiro manual para os dois perfis:

1. Crie uma conta USER e uma ADMIN conforme acima, exigindo troca da senha inicial.
2. Acesse o frontend em janela privada: deve abrir o login do Keycloak e retornar ao layout após autenticar.
3. Com USER, consulte Times/Jogadores: cadastros, edição e exclusão não aparecem. Acessar diretamente uma rota de edição redireciona para consulta; chamadas POST/PUT/DELETE à API retornam 403.
4. Com ADMIN, cadastre, edite e exclua um time sem jogadores; depois cadastre/edite/exclua um jogador. Recarregue para conferir persistência.
5. Com ambos, pergunte sobre FURIA, NAVI, KSCERATO, top 5 e rating médio. Teste time inexistente e jogador ausente. Com IA desativada, verifique a mensagem de indisponibilidade; não confunda com ausência de dados.
6. Tente solicitar exclusão ou SQL pelo chat: nenhuma ferramenta de escrita deve ser executada.
7. Faça logout e confirme que acessar uma rota exige novo login. Encerre a sessão pelo Keycloak e aguarde expiração/refresh para verificar o redirecionamento.
8. Confira no navegador que não há JWT em localStorage/sessionStorage e que Bearer é enviado somente à API configurada. Em dispositivo móvel, confirme que chat e navegação continuam utilizáveis.

### Builds em ambientes com proxy

Os Dockerfiles aceitam secrets opcionais do BuildKit: `ca_bundle`, `java_cacerts` e `maven_settings`. Eles permitem usar as autoridades certificadoras e o proxy corporativo sem desabilitar TLS nem embutir credenciais nas imagens. Passe também os build args predefinidos `HTTP_PROXY`, `HTTPS_PROXY`, `NO_PROXY`; quando necessário, configure a resolução do hostname do proxy no build. Em uma rede comum, nenhum desses secrets é necessário. Não versione configurações locais que contenham credenciais.
