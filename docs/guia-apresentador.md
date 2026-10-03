# Guia detalhado de apoio — operação e falas da demo

> **Agenda anterior substituída.** Este documento é um banco de falas, instruções e contingências para ensaio. As marcações de tempo abaixo são da versão extensa e não devem ser seguidas em palco. Use a [apresentação integrada de 60 minutos](apresentacao-integrada-60-min.md), com demo de 25 minutos, como roteiro principal.

**Público:** equipe técnica e gestores da SEF-MG. **Ambiente ensaiado:** MTA 7.3.2 sobre OpenShift.

Este roteiro usa exemplos fictícios e resultados reais do laboratório. As falas são sugestões: use-as como apoio, com suas palavras. Não pressuponha que o ambiente real da Secretaria tem os mesmos problemas.

## Mensagem que deve atravessar a apresentação

> “Modernizar começa por entender o portfólio. O MTA ajuda a transformar evidências do código em decisões de migração, ações para as equipes e um processo que pode ser repetido.”

Ao terminar, o cliente deve conseguir responder: por onde começar, quais mudanças precisam ser investigadas e como verificar progresso sem perder de vista o risco de negócio.

## Cola de uma página

| Tempo | Tela/atividade | Frase-chave |
|---|---|---|
| 0–5 | Abertura e contexto | “Quero conectar portfólio, arquitetura e código.” |
| 5–12 | Inventory e assessment | “Pouco esforço técnico não significa pouco risco de negócio.” |
| 12–19 | Portal local funcionando e restart | “O comportamento revela dependências que um build bem-sucedido não mostra.” |
| 19–25 | Configuração da análise | “O resultado depende do destino e do escopo escolhidos.” |
| 25–37 | Findings reais do portal | “Cada finding precisa virar uma decisão e um critério de aceite.” |
| 37–44 | Regra corporativa | “Conhecimento da organização pode se tornar uma verificação repetível.” |
| 44–49 | Plano de remediação | “Reanalisar mede parte do progresso; testes validam o comportamento.” |
| 49–55 | Batch e ondas de migração | “A escolha do piloto combina evidências técnicas e contexto de negócio.” |
| 55–60 | Perguntas e fechamento | “Vamos escolher uma aplicação e um destino para um piloto verificável.” |

**Não dependa de uma análise nova terminar ao vivo.** Já existem duas análises concluídas. O roteiro principal usa esses resultados e não exige escrever código na frente do cliente. A remediação ao vivo é um módulo opcional, descrito ao final.

## Preparação — no dia anterior e 20 minutos antes

### Abas que devem estar abertas

1. [MTA — Application inventory](https://mta-mta.apps.cluster-7vkrl.dyn.redhatworkshops.io/applications).
2. [Gitea — projeto da demo](https://gitea.apps.cluster-7vkrl.dyn.redhatworkshops.io/lab-user/sef-mg-mta-demo).
3. [Código do portal](https://gitea.apps.cluster-7vkrl.dyn.redhatworkshops.io/lab-user/sef-mg-mta-demo/src/branch/main/portal-arrecadacao/src/main/java/br/gov/mg/sef/demo/PortalServlet.java).
4. [Regra corporativa](https://gitea.apps.cluster-7vkrl.dyn.redhatworkshops.io/lab-user/sef-mg-mta-demo/src/branch/main/rules/sef-corporativo/mainframe.yaml).
5. [Resultados reais e evidências](https://gitea.apps.cluster-7vkrl.dyn.redhatworkshops.io/lab-user/sef-mg-mta-demo/src/branch/main/docs/results-mta-7.3.2.md).
6. Portal local em **http://127.0.0.1:8080/**, depois de iniciá-lo.

Os endereços do workshop podem expirar; confira acesso antes do evento. No MTA, localize os nomes começando por `SEF-MG DEMO`. Há outras aplicações no inventário: não as selecione para a demo. As aplicações criadas têm IDs 11 e 12. Assessment e Review ainda não foram preenchidos; Analysis está Completed.

### Preparar a execução local

Use JDK 17 e Maven 3.9.x. A aplicação não foi implantada no OpenShift: **o MTA está no cluster; o portal da demonstração roda localmente**.

Na raiz de uma cópia do projeto:

```bash
mvn clean package
mvn -pl conciliacao-batch exec:java
mvn -pl portal-arrecadacao jetty:run
```

O último comando fica rodando. Em outro terminal, na mesma raiz:

```bash
python3 scripts/smoke.py
```

Resultado esperado: `OK: sessões isoladas, recibo persistido localmente, valores e validações, batch.`

Se ainda não tiver uma cópia local, clone o repositório Gitea em uma pasta nova e abra sua raiz antes desses comandos. Não faça downloads de dependências pela primeira vez durante a apresentação.

### Conferências finais

- No MTA, confirme `Completed` para as duas aplicações e encontre previamente os quatro findings que vai abrir: IP, filesystem, imports Jakarta e regra corporativa.
- Deixe o terminal do portal separado do terminal de comandos. Evite expor histórico de autenticação durante o compartilhamento de tela.
- Abra uma janela privativa do navegador para mostrar uma segunda sessão independente.
- Salve uma cópia local deste guia e de `docs/results-mta-7.3.2.md`. Tire capturas dos findings no ensaio se precisar de contingência visual.
- Ensaie a navegação até a análise do portal: conforme o layout, ela aparece no detalhe da aplicação ou no acesso ao status `Completed`. Use os nomes dos findings, não dependa da ordem das linhas.
- Não prepare respostas inventadas no assessment. Se mostrar o questionário, explique que as respostas reais precisam ser construídas com os responsáveis.

---

## 1. Abertura — 0 a 5 minutos

**Na tela:** inventário do MTA, sem abrir código ainda.

**Diga:**

> “Hoje eu quero mostrar como podemos organizar uma jornada de modernização: entender as aplicações, escolher uma estratégia e transformar os problemas encontrados em trabalho executável pelas equipes.”
>
> “Vou usar dois sistemas pequenos e fictícios. Eles não representam sistemas reais da Secretaria. Fizemos isso para deixar visíveis, em uma hora, alguns desafios frequentes de aplicações legadas.”
>
> “O destino que vamos discutir é OpenShift. Mas antes de falar em container, precisamos entender onde a aplicação guarda estado, como se integra e de qual runtime depende.”

**Pergunta curta ao público:**

> “Hoje, o maior desafio de vocês está em conhecer o portfólio, dimensionar as mudanças ou executar a modernização com segurança?”

Ouça uma ou duas respostas por até um minuto. Use a resposta para dar mais ênfase ao inventory ou aos findings, mantendo o roteiro.

**Transição:**

> “Vamos começar pela unidade de decisão: a aplicação e seu contexto de negócio.”

## 2. Inventory e assessment — 5 a 12 minutos

**Faça:**

1. Localize `SEF-MG DEMO - Portal de arrecadacao` e `SEF-MG DEMO - Conciliacao batch`.
2. Mostre que cada uma tem um cadastro e uma análise separados, embora compartilhem o mesmo repositório.
3. Abra o cadastro do portal e mostre seu repositório, branch `main` e caminho `portal-arrecadacao`.
4. Mostre as dimensões de assessment/review e, se tiver ensaiado a navegação, abra o questionário sem preenchê-lo ao acaso.
5. Volte ao inventário.

**Diga:**

> “O portal representa uma aplicação de atendimento. O batch representa uma rotina de retaguarda. Ambos foram escritos em Java, mas o modo de operar, a tolerância a falhas e a estratégia de migração podem ser diferentes.”
>
> “O inventário permite reunir essas informações. Parte vem da análise técnica; parte precisa vir de quem conhece o negócio. Criticidade, dono da aplicação e tolerância a indisponibilidade não podem ser deduzidos apenas lendo o código.”
>
> “O assessment ajuda a estruturar essa conversa. Por exemplo: esse recibo pode ser perdido? Uma execução de conciliação pode ser repetida? Quem depende desse resultado?”

**Mostre a separação:**

> “Aqui o assessment está não iniciado e a análise técnica está concluída. Isso é intencional: analisar código não equivale a concluir a avaliação de negócio.”

**Não afirme:** que o batch consome arquivos do portal. Neste laboratório, os dois usam dados próprios; a integração entre eles é uma hipótese para evolução.

**Transição:**

> “Agora vamos ver uma aplicação que funciona e, mesmo assim, traz decisões importantes para a migração.”

## 3. Portal funcionando — 12 a 19 minutos

**Na tela:** http://127.0.0.1:8080/.

**Faça e diga:**

1. **Mostre a página.**

   > “Este é um portal mínimo de arrecadação fictícia. Não existem dados de contribuintes nem conexão com sistemas reais.”

2. **Envie o valor `100.00`.** Mostre o nome do último recibo.

   > “O portal calcula um valor demonstrativo e grava um recibo. Para a perspectiva funcional básica, a operação deu certo.”

3. **Atualize a página.** Mostre o contador de visitas e o último recibo.

   > “O contador e a referência ao recibo ficam na sessão HTTP, dentro desta JVM.”

4. **Abra a mesma URL em janela privativa.** Ela começa com outra sessão.

   > “Esta janela tem outra sessão e outro estado. Isso mostra o uso de sessão; ainda não é um teste com duas réplicas.”

5. **No terminal do portal, pressione Ctrl+C.** Reinicie:

   ```bash
   mvn -pl portal-arrecadacao jetty:run
   ```

6. **Atualize a janela original.** O contador recomeça e o último recibo aparece como `nenhum`.

   > “Ao reiniciar o processo, perdemos a informação da sessão. O arquivo ainda pode existir nesta máquina, porque reiniciar uma JVM não apaga o disco do host. Em um pod substituído sem armazenamento persistente, o arquivo local também pode desaparecer.”

7. **Opcional: mostre os arquivos, sem apagar nada:**

   ```bash
   ls -lt /tmp/sef-mg-demo/recibos/
   ```

**Conclusão deste passo:**

> “Para ir a OpenShift, precisamos decidir o que é temporário, o que é dado de negócio e o que precisa ser compartilhado entre instâncias. Apenas criar uma imagem não responde a essas perguntas.”

**Precisão essencial:**

> “Nesta análise específica, o MTA detectou o uso de filesystem, mas não gerou finding de sessão. Estou mostrando a sessão pelo comportamento da aplicação, como parte da revisão arquitetural.”

## 4. Como a análise foi configurada — 19 a 25 minutos

**Na tela:** MTA, selecione somente o portal e clique `Analyze`.

**Percorra o assistente:**

1. **Analysis mode:** selecione `Source code`.
2. **Set targets:** selecione `Containerization` e `Jakarta EE 9`.
3. **Scope:** `Application and internal dependencies only`.
4. **Advanced → Custom rules → Repository:**

   | Campo | Valor |
   |---|---|
   | Repository type | Git |
   | Source repository | `https://gitea.apps.cluster-7vkrl.dyn.redhatworkshops.io/lab-user/sef-mg-mta-demo.git` |
   | Branch | `main` |
   | Root path | `rules/sef-corporativo` |
   | Associated credentials | Não necessário para este repositório público de exemplos |

5. **Advanced options:** confira os targets, mantenha tagging habilitado e enhanced details desabilitado, como no ensaio.
6. **Review:** mostre o resumo.

**Diga:**

> “A análise precisa de uma pergunta. Aqui temos duas: o que merece atenção para containerizar e o que muda ao adotar Jakarta EE? São objetivos relacionados, mas diferentes.”
>
> “OpenShift não obriga toda aplicação a mudar de `javax` para `jakarta`. Essa mudança depende do runtime de destino. Selecionamos Jakarta para demonstrar essa dimensão separadamente.”
>
> “Esta execução usa código-fonte. Uma avaliação mais ampla pode incluir dependências. O escopo escolhido influencia a cobertura e o tempo de análise.”

**Recomendação para a sessão principal:** clique `Cancel` depois de mostrar o resumo e abra a análise já concluída.

> “Este é o mesmo perfil utilizado no ensaio. Vou abrir o resultado já concluído para concentrarmos nosso tempo na interpretação.”

**Alternativa ensaiada:** clique `Run` se quiser demonstrar a fila. Anote a tarefa e siga para os resultados salvos enquanto ela executa; a nova execução pode mudar o status visível da aplicação. Não prometa duração fixa.

## 5. Findings do portal — 25 a 37 minutos

**Abra a análise concluída do portal.** Resultado observado: **8 tipos de issues, 14 ocorrências e 16 pontos de esforço**.

**Explique em 30 segundos:**

> “Um tipo de issue pode aparecer em vários locais do código. Por isso temos oito tipos e quatorze ocorrências. A pontuação é um indicador técnico produzido pelas regras; não significa dezesseis dias de trabalho.”

### 5.1 IP hardcoded — cerca de 2 minutos

**Abra:** `hardcoded-ip-address`, `PortalServlet.java`, linha 17.

**Diga:**

> “Aqui existe um endereço IP dentro do código. É um endereço reservado para exemplos, mas o padrão representa acoplamento a uma infraestrutura específica. Se o destino mudar, a equipe precisa alterar código ou reconstruir a aplicação.”
>
> “A ação seria externalizar a configuração e usar uma forma estável de descoberta do serviço. O critério de aceite é mudar o destino por configuração, com validação e tratamento adequado de falhas.”

**Evite:** “Todo hostname fixo é um erro.” Um nome estável pode ser uma escolha válida; o contexto e a configuração importam.

### 5.2 Filesystem local — cerca de 3 minutos

**Abra:** `local-storage-00001`, linhas 45 e 48.

**Diga:**

> “O finding liga o risco que vimos no comportamento a estas linhas que criam e escrevem arquivos. A recomendação depende da finalidade do dado: log, cache, temporário ou documento que precisa ser preservado.”
>
> “Para um recibo que deva ser recuperado depois, precisamos de persistência e de um contrato de acesso. Isso pode levar a banco, object storage ou volume persistente, conforme os requisitos. A ferramenta aponta a evidência; a escolha arquitetural vem depois.”

**Critério de aceite para mostrar:** recibo recuperável após reinício/substituição e comportamento correto com duas réplicas.

### 5.3 Java EE para Jakarta — cerca de 4 minutos

**Abra:** `javax-to-jakarta-import-00001`. Mostre um import Servlet nas linhas 6–10. Depois abra um finding de dependência e o `web.xml` na linha 2.

**Diga:**

> “A mudança de runtime aparece em mais de uma camada: imports, dependências e descritor. Se eu trocar apenas o nome do pacote, posso produzir uma aplicação que compila ou implanta incorretamente.”
>
> “O backlog precisa incluir a atualização do runtime e testes de comportamento. Automatizações podem ajudar nas mudanças mecânicas, mas precisam ser compatíveis com o destino escolhido.”

Não demonstre OpenRewrite como já executado: nenhuma transformação foi aplicada neste laboratório. O Jetty 9 local usa a API Servlet antiga; ele também precisaria mudar para executar a versão Jakarta.

### 5.4 Feche conectando finding e trabalho — cerca de 3 minutos

> “Para cada evidência, queremos uma ação com responsável, decisão de arquitetura e critério de aceite. Corrigir imports é diferente de escolher uma estratégia de persistência. Os dois podem aparecer no mesmo relatório, mas exigem trabalhos diferentes.”

**Transição:**

> “Até aqui usamos conhecimento das regras do produto. E quando a regra depende da política da organização?”

## 6. Regra corporativa — 37 a 44 minutos

**Faça:** abra o YAML no Gitea e, em seguida, `sef-mainframe-direto-00001` no relatório do portal, linha 18.

**Diga:**

> “Criamos uma política fictícia para o laboratório: novas integrações não devem acessar diretamente um hostname de mainframe; devem passar por uma API institucional. Isso não é uma norma real da Secretaria.”
>
> “A regra procura um padrão simples nos arquivos Java. Quando encontra o hostname, registra uma orientação para o time. Assim, um conhecimento que antes podia ficar apenas em um documento ou com uma pessoa passa a ser verificado de forma repetível.”

**Mostre somente quatro campos:**

- `ruleID`: identificador estável para rastrear a política.
- `category`: `mandatory` nesta política fictícia.
- `message`: orientação de remediação.
- `when → builtin.filecontent`: condição que procura o hostname em arquivos Java.

**Diga sobre o esforço:**

> “Atribuímos três pontos à regra para a demo. Numa organização, esse peso precisaria ser calibrado com a experiência das equipes.”

**Mostre que apareceu também no batch:** uma ocorrência na linha 16 de `ConciliacaoBatch.java`.

> “Aplicamos o mesmo conhecimento a duas aplicações. Esse é o ganho que queremos explorar num portfólio maior.”

**Limite da regra, em uma frase:**

> “Esta é uma busca textual: ela pode encontrar comentários e deixar de encontrar um endpoint montado dinamicamente. A regra precisa evoluir junto com os padrões da organização.”

## 7. Remediação e verificação — 44 a 49 minutos

**Na tela:** `docs/remediation.md` no repositório; não altere a baseline durante o roteiro principal.

**Diga:**

> “O relatório vira um backlog. Podemos começar por configuração, depois tratar persistência e sessão, escolher o runtime e validar o comportamento. A sequência depende das decisões e das dependências entre as mudanças.”
>
> “Depois de cada mudança, compilamos, testamos e repetimos a análise com o mesmo perfil. Isso permite comparar resultados sem confundir uma mudança de escopo com uma melhora no código.”
>
> “Se eu trocar o hostname, a regra textual pode desaparecer. Isso demonstra o funcionamento da regra, mas não comprova que implementamos uma API institucional, autenticação, timeout ou resiliência.”

**Use três critérios de aceite concretos:**

| Mudança | Como validar |
|---|---|
| Externalizar configuração | Trocar o destino sem recompilar e rejeitar configuração inválida |
| Resolver persistência/estado | Recuperar dados após restart e testar duas réplicas |
| Migrar runtime | Compilar, implantar no runtime escolhido e passar os testes funcionais |

**Deixe claro:** a versão modernizada ainda não está pronta. Nesta sessão mostramos diagnóstico, plano e critérios. Se desejar código antes/depois, prepare e ensaie o módulo opcional abaixo antes da apresentação.

## 8. Batch e ondas de migração — 49 a 55 minutos

**Faça:** abra a análise de `SEF-MG DEMO - Conciliacao batch`.

Resultado observado: **7 tipos de issues, 9 ocorrências e 15 pontos de esforço**. Mostre JavaMail, arquivo local e a mesma regra corporativa. Não percorra todos os findings novamente.

**Diga:**

> “O batch compartilha alguns problemas com o portal, mas sua operação é diferente. Para levá-lo a um Job ou CronJob, precisamos discutir reexecução, idempotência, concorrência e durabilidade da saída.”
>
> “O portal marcou dezesseis pontos e o batch quinze. Essa proximidade não permite escolher o piloto automaticamente. Criticidade, janela operacional, integrações e capacidade de testar podem pesar mais do que essa diferença.”

**Sobre priorização:**

> “Uma hipótese seria usar o batch como piloto e o portal numa onda seguinte, após resolver estado. Mas se o batch tiver uma janela crítica ou dependências difíceis, a ordem pode mudar. O assessment valida essa hipótese.”

Se abrir Migration waves, apresente o recurso como lugar para organizar a decisão; **não diga que já criamos ondas específicas da demo**. Elas ainda não foram cadastradas.

**Exemplo de revisão humana — opcional, 45 segundos:**

> “Este finding menciona `web.xml`, mas aponta para o POM do batch, que não possui `web.xml`. É um candidato a falso positivo ou imprecisão. A equipe precisa revisar e classificar o resultado, em vez de converter automaticamente toda ocorrência em tarefa.”

Regra em questão: `javax-to-jakarta-servlet-00130`.

## 9. Fechamento e perguntas — 55 a 60 minutos

**Volte ao inventário.**

**Diga:**

> “Começamos com duas aplicações funcionando, analisamos seus caminhos de modernização, adicionamos uma política fictícia e transformamos as evidências em ações e critérios de aceite.”
>
> “Para um piloto real, eu proporia escolher uma aplicação com responsável disponível, um destino definido e testes suficientes para comparar antes e depois. A partir daí, avaliamos o que é reutilizável no restante do portfólio.”

**Pergunta final:**

> “Qual aplicação de vocês teria hoje contexto de negócio e condições de teste suficientes para começar esse piloto?”

Reserve espaço para ouvir a resposta. Termine com uma proposta concreta de próximo passo, sem prometer estimativa ou prazo antes de conhecer a aplicação.

---

## Respostas curtas para perguntas prováveis

**“O MTA migra a aplicação sozinho?”**

> “A análise identifica evidências e orientações. Há capacidades de transformação que podem ajudar em mudanças específicas, mas migração concluída exige runtime, configuração, dados e comportamento validados. Nesta demo executamos análise, não transformação automática.”

**“A aplicação precisa estar rodando para analisar?”**

> “Nesta análise de fonte, não. O MTA baixou o repositório. Rodamos o portal separadamente para explicar o impacto dos padrões encontrados.”

**“Podemos analisar sem fornecer o fonte?”**

> “Existe análise binária, mas a cobertura e a navegação mudam. A nossa regra corporativa procura `.java` e foi validada com fonte. Não prometo resultado idêntico ao analisar apenas um WAR.”

**“Esses pontos dão o prazo do projeto?”**

> “Não diretamente. São uma referência técnica das regras. Precisamos acrescentar testes, integrações, dados, decisões arquiteturais e calibrar com a produtividade do time.”

**“Todo legado precisa virar microsserviço?”**

> “Não. Podemos manter a estrutura da aplicação e modernizar runtime, configuração e operação. A decomposição deve responder a uma necessidade concreta.”

**“Basta pôr um volume persistente?”**

> “Pode resolver uma parte da durabilidade. Ainda precisamos verificar acesso concorrente, disponibilidade, backup, retenção e se duas réplicas usam corretamente esses dados.”

**“Isso faz análise de vulnerabilidades?”**

> “O objetivo aqui é migração e modernização. Bibliotecas antigas merecem uma avaliação de composição e segurança própria; não vou tratar ausência de finding como ausência de vulnerabilidade.”

**“O código sai do ambiente?”**

> “Neste ensaio, o MTA no cluster buscou o fonte no Gitea do laboratório. Não usamos recurso de IA na análise. Para um projeto real, precisamos revisar fluxos de rede, repositórios, dependências e as configurações escolhidas antes de afirmar onde os dados podem transitar.”

**“Precisamos trocar Java EE por Jakarta para ir ao OpenShift?”**

> “Depende do runtime de destino. Containerização e migração de APIs são decisões diferentes. Combinamos os targets para mostrar os dois tipos de trabalho.”

## Módulo opcional — alteração ao vivo, somente após ensaio

Este módulo substitui parte dos minutos 37–49; não acrescente tempo ao roteiro. **Não foi executado ainda** e não há relatório comparativo pronto. Não abra esse compromisso durante a sessão se não tiver ensaiado.

1. Em uma cópia de trabalho limpa, crie a branch `demo/remediacao-regra`.
2. No portal, substitua apenas `mainframe.sef-demo.invalid` por `api.sef-demo.invalid`.
3. Compile e execute o smoke test. Commit e publique a branch no Gitea.
4. Cadastre uma aplicação adicional, por exemplo `SEF-MG DEMO - Portal experimento`, apontando para essa branch e para `portal-arrecadacao`. Preserve o cadastro e a análise da baseline.
5. Use exatamente o perfil da baseline: Source code, mesmos targets, mesmo escopo e mesma regra corporativa.
6. Confirme se o finding corporativo desapareceu e compare os demais resultados. Só apresente números depois dessa verificação.

**Fala obrigatória:**

> “Esta alteração valida a sensibilidade da regra. Não implementa a integração modernizada. Estamos separando o teste da regra do trabalho arquitetural que a recomendação exige.”

## Plano de contingência

| Situação | O que fazer | O que dizer |
|---|---|---|
| Análise nova demora | Abra os resultados do ensaio e siga com interpretação | “Vamos usar a execução concluída com este mesmo perfil.” |
| Cluster indisponível | Use guia local, evidências JSON e capturas preparadas | “Vou mostrar os resultados registrados no ensaio; o ambiente está indisponível agora.” |
| Portal não inicia | Use fonte e resultado do smoke do ensaio; pule restart | “O comportamento foi validado no ensaio. Vou explicar a dependência de estado no código.” |
| Interface mudou de disposição | Busque nomes das aplicações/regras | “Vamos navegar pela evidência que queremos investigar.” |
| Finding ausente | Compare perfil, revisão, modo e logs | “A ausência precisa ser investigada; não é prova de que o problema não existe.” |
| Restam apenas 15 minutos | Mostre IP, filesystem, regra corporativa e fechamento | Corte a segunda execução, detalhamento XML e módulo opcional |

## Números e limites para manter à mão

- Portal: aplicação **11**, análise **2**, tarefa **5**, **8 tipos / 14 ocorrências / esforço 16**.
- Batch: aplicação **12**, análise **1**, tarefa **6**, **7 tipos / 9 ocorrências / esforço 15**.
- Uma ocorrência da regra corporativa por aplicação.
- Tasks de análise e descoberta terminaram com sucesso no ensaio.
- Fontes analisados: commit `b0696409a7f2b8e8fb8f9aa0f84c41bb20b65503`.
- Não demonstrado: deploy das aplicações no OpenShift, versão modernizada, teste de duas réplicas, transformação OpenRewrite ou assessment de negócio preenchido.
- Não detectado nesta execução: finding específico de sessão HTTP e de configuração embutida.

Documentação de apoio: [resultados reais](results-mta-7.3.2.md), [backlog](remediation.md), [validação](validation.md) e [guia oficial da interface MTA 7.3](https://docs.redhat.com/en/documentation/migration_toolkit_for_applications/7.3/html-single/user_interface_guide/user_interface_guide).
