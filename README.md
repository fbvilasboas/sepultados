# Consulta Sepultados Uberaba (App Android)

App Android nativo (Kotlin) que abre o sistema oficial de consulta de sepultados
da Prefeitura de Uberaba (Codiub) dentro de uma WebView, com aparência de app:
ícone próprio, sem barra de endereço, indicador de carregamento, "puxar para
atualizar" e tratamento de erro de conexão.

URL carregada: https://app3.codiub.com.br/consulta_sepultados/#/sepultados

## Gerar o APK automaticamente (sem instalar nada no seu PC)

Este projeto já vem com um workflow do GitHub Actions (`.github/workflows/build.yml`)
que compila o APK sozinho, na nuvem, toda vez que você enviar o código pro GitHub.

Passo a passo:

1. Crie uma conta gratuita em https://github.com (se ainda não tiver)
2. Clique em **New repository** (repositório novo), dê um nome (ex: `consulta-sepultados-app`)
   e deixe como **Private** ou **Public**, sem marcar nenhuma opção de inicialização
3. Na página do repositório vazio, clique em **uploading an existing file**
   (ou "Add file → Upload files")
4. Abra a pasta `CemiterioApp` no seu computador e **arraste todo o conteúdo dela**
   (incluindo a pasta oculta `.github`) para a área de upload do GitHub
   - Importante: envie o *conteúdo* da pasta `CemiterioApp`, não a pasta zipada
5. Clique em **Commit changes**
6. Vá na aba **Actions** do repositório — vai aparecer um workflow rodando
   automaticamente ("Build APK"). Aguarde uns 3-5 minutos
7. Quando terminar (ícone verde ✅), clique no run finalizado → na seção
   **Artifacts**, baixe o arquivo `app-debug-apk.zip`
8. Extraia esse zip: dentro está o `app-debug.apk`, pronto para instalar no celular

Você pode repetir esse processo sempre que quiser gerar uma nova versão: basta
editar os arquivos no GitHub (ou reenviar) e o Actions compila de novo automaticamente.

## Como abrir e gerar o APK (alternativa: pelo computador, Android Studio)

1. Instale o **Android Studio** (gratuito): https://developer.android.com/studio
2. Abra o Android Studio → **Open** → selecione a pasta `CemiterioApp`
3. Aguarde o Gradle sincronizar (primeira vez pode baixar dependências, precisa de internet)
4. Para testar: conecte um celular Android (com "Depuração USB" ativada) ou use um emulador,
   e clique em **Run ▶**
5. Para gerar o APK final: menu **Build → Build Bundle(s) / APK(s) → Build APK(s)**
   O arquivo `app-debug.apk` aparece em `app/build/outputs/apk/debug/`
6. Para instalar no celular: copie o APK para o aparelho e abra o arquivo
   (pode ser necessário permitir "instalar de fontes desconhecidas")

## Estrutura

- `app/src/main/java/br/com/codiub/cemiterio/MainActivity.kt` → toda a lógica do app
- `app/src/main/res/layout/activity_main.xml` → tela (WebView + barra de progresso + tela de erro)
- `app/src/main/AndroidManifest.xml` → permissão de internet e configuração do app

## Personalizar

- **Nome do app**: `app/src/main/res/values/strings.xml` → `app_name`
- **Cores**: `app/src/main/res/values/colors.xml`
- **Ícone**: substitua os arquivos `ic_launcher.png` em cada pasta `mipmap-*`
  (foi gerado um ícone simples com uma cruz; recomendo trocar por um ícone
  personalizado, por exemplo feito no Canva ou Figma, exportado em PNG nos
  tamanhos: 48x48, 72x72, 96x96, 144x144 e 192x192 px)

## Observação importante

Este app funciona como uma "casca" nativa em volta do site oficial — ou seja,
toda a lógica de busca, listagem de nomes e o popup com os detalhes/foto do
sepultamento continuam sendo os mesmos do site (https://app3.codiub.com.br).
Isso garante que o app sempre reflita os dados reais e atualizados do sistema
da Prefeitura, sem duplicar ou hospedar essas informações em outro lugar.

Se no futuro você quiser uma versão 100% nativa (telas próprias, mais rápida,
com histórico offline etc.), será necessário identificar o endereço (URL) da
API que o site chama internamente ao pesquisar — isso pode ser obtido pela
aba "Network" das Ferramentas de Desenvolvedor do Chrome.
