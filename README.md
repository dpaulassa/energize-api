# Projeto: Energize API - Cidades ESG Inteligentes

Este projeto consiste na implementação de um pipeline de CI/CD completo para a aplicação "Energize API", desenvolvida em Java com Spring Boot. O objetivo foi automatizar todo o ciclo de vida da aplicação, desde o commit do código até o deploy em um ambiente de nuvem, utilizando práticas modernas de DevOps.

## 🚀 Tecnologias Utilizadas

* **Linguagem:** Java 21
* **Framework:** Spring Boot 3
* **Banco de Dados:** MongoDB
* **Build:** Maven
* **Containerização:** Docker & Docker Compose
* **Cloud:** Microsoft Azure (Máquina Virtual Ubuntu 22.04 LTS)
* **CI/CD:** GitHub Actions
* **Servidor Web / Proxy Reverso:** Nginx

## 🛠️ Como Executar Localmente com Docker

Para executar este projeto em seu ambiente local, certifique-se de ter o Docker e o Docker Compose instalados.

1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/dpaulassa/energize-api.git](https://github.com/dpaulassa/energize-api.git)
    cd energize-api
    ```

2.  **Crie o arquivo de variáveis de ambiente:**
    Crie um arquivo chamado `.env` na raiz do projeto e adicione o seguinte conteúdo. Este arquivo é lido pelo Docker Compose para configurar os containers.
    ```
    DATABASE_URL=mongodb://mongo:27017/energizeplus
    ```

3.  **Suba os containers:**
    Execute o comando abaixo para construir as imagens e iniciar os containers em segundo plano.
    ```bash
    docker compose up -d
    ```

4.  **Acesse a aplicação:**
    A aplicação estará disponível em `http://localhost:8080/api/users`.

## ⚙️ Pipeline de CI/CD

O pipeline de automação foi construído utilizando **GitHub Actions**. Ele é definido no arquivo `.github/workflows/main.yml` e é acionado a cada `push` na branch `main`.

O pipeline é dividido em duas etapas (jobs) principais:

1.  **`build-and-push`**:
    * Este job é responsável por fazer o checkout do código.
    * Realiza o login no Docker Hub de forma segura, utilizando segredos (secrets) configurados no repositório.
    * Constrói a imagem Docker da aplicação a partir do `Dockerfile`.
    * Envia (push) a nova imagem gerada para o Docker Hub, versionada com a tag `latest`.

2.  **`deploy-to-staging`**:
    * Este job só é executado se o `build-and-push` for concluído com sucesso.
    * Ele se conecta via SSH à Máquina Virtual no Azure, utilizando segredos para autenticação.
    * Uma vez conectado, ele executa um script que:
        * Navega até a pasta do projeto.
        * Baixa (`pull`) a imagem mais recente que acabamos de enviar para o Docker Hub.
        * Reinicia os containers (`docker compose up -d`), forçando o Docker a usar a nova imagem baixada, atualizando a aplicação em staging sem downtime perceptível.

## 🐳 Containerização

A aplicação foi totalmente containerizada para garantir portabilidade e consistência entre os ambientes.

### Dockerfile

Foi utilizado um `Dockerfile` multi-stage para otimização da imagem final:
* **Estágio 1 (Builder):** Utiliza uma imagem completa do Maven e Java para compilar o projeto e gerar o arquivo `.jar`.
* **Estágio 2 (Final):** Inicia a partir de uma imagem Java Runtime (JRE) muito mais leve e copia apenas o `.jar` compilado do estágio anterior. Isso resulta em uma imagem final menor, mais segura e otimizada para produção, sem incluir as ferramentas de build.

### Docker Compose

O `docker-compose.yml` orquestra os dois serviços principais da aplicação:
* **`api`**: O container da nossa aplicação Java/Spring Boot.
* **`mongo`**: O container do banco de dados MongoDB.
* **Recursos:** Foram utilizados `networks` para permitir a comunicação segura entre a API e o banco de dados, `volumes` para garantir a persistência dos dados do MongoDB mesmo que o container seja recriado, e `env_file` para carregar as variáveis de ambiente.

## 🖼️ Prints do Funcionamento

**Pipeline executando com sucesso no GitHub Actions:**

![Pipeline executando com sucesso](./docs/prints/pipeline-sucesso.png)

**Ambiente de Staging funcionando:**

![API rodando no ambiente de staging](./docs/prints/api-staging.png)

**Teste de criação de usuário com o Postman:**

![Teste com Postman POST](./docs/prints/teste-postman-post.png)
![Teste com Postman GET](./docs/prints/teste-postman-get.png)