# Untitled

# EcoLight

## **Descrição**

O **EcoLight** é um aplicativo desenvolvido para gerenciar e monitorar o consumo energético doméstico, ajudando os usuários a economizar energia e promover um uso sustentável. Ele permite cadastrar dispositivos, registrar consumos, configurar metas mensais e obter insights sobre o uso eficiente de energia.

---

- **Link do Vídeo:**
    - [https://youtu.be/YSCVVKc8_bA](https://youtu.be/YSCVVKc8_bA)

---

## **Funcionalidades**

- Cadastro, edição e exclusão de dispositivos.
- Registro e monitoramento de consumo por dispositivo.
- Configuração e acompanhamento de metas mensais.
- Cálculo automático de consumo total e impostos.
- Integração com Firebase e API REST.

---

## **Requisitos**

- **Plataforma**:
    - Android 5.0 (Lollipop) ou superior.
- **Tecnologias**:
    - Kotlin, Firebase Authentication, Realtime Database, Retrofit e PostgreSQL.

---

## **Instalação**

1. **Clonar o Repositório**:
    
    ```bash
    git clone <https://github.com/jvs4nt/EcoLight.git>
    
    ```
    

### Passo 2: Abrir no Android Studio

1. Abra o **Android Studio**.
2. Selecione **File > Open** e escolha a pasta clonada.
3. Aguarde o carregamento das dependências.

### Passo 3: Configurar Firebase

1. Baixe o arquivo `google-services.json` do console Firebase.
2. Coloque o arquivo na pasta `app/` do projeto.

### Passo 4: Configurar a URL da API

Altere a constante `BASE_URL` no arquivo `RetrofitClient` para o endereço do backend:

```kotlin
kotlin
Copiar código
private const val BASE_URL = "https://consumoenergiaapi.onrender.com/"

```

### Passo 5: Executar o Aplicativo

1. Conecte um dispositivo Android ou configure um emulador.
2. Execute o comando **Run > Run 'app'** no Android Studio.

---

## **Fluxo do Aplicativo**

1. **Tela de Login**:
    - Login ou registro de novos usuários via Firebase.
2. **Tela Inicial**:
    - Exibição e criação de metas mensais.
    - Navegação para registro de consumo e gerenciamento de dispositivos.
3. **Tela de Registro de Consumo**:
    - Registro de consumo por dispositivo com tempo de uso.
4. **Tela de Lista de Consumos**:
    - Visualização de consumos registrados com opções de edição e exclusão.
5. **Tela de Configuração de Metas**:
    - Criação ou atualização de metas mensais.