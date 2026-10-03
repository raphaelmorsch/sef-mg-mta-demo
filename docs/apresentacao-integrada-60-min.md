# Apresentação integrada — Do legado ao OpenShift

**Roteiro principal · SEF-MG · 60 minutos no total · demo de 25 minutos**

Título recuperado do chat original: **Do legado ao OpenShift: modernização de aplicações em escala com Red Hat Migration Toolkit for Applications**.

Este documento substitui a distribuição de tempo anterior, que ocupava a hora inteira com demonstração. Foi elaborado após recuperar o conteúdo completo do chat “Apresentação sobre modernização”. Preserva a narrativa portfólio → aplicação → código e os temas de boas práticas, governança e ondas de migração. A distribuição de tempo foi revista conforme sua orientação; não é uma transcrição da agenda original, que reservava 38 minutos a blocos de demo.

A apresentação usa **MTA 7.3.2**, versão efetivamente instalada. Referências a versões posteriores, automações e IA no chat original não são evidência de disponibilidade no laboratório.

## Agenda fechada

| Tempo | Formato | Tema |
|---|---|---|
| 00–05 | Apresentação | Problema e objetivo da modernização |
| 05–12 | Apresentação | Papel do MTA: portfólio → aplicação → código |
| 12–18 | Apresentação | Boas práticas para uma plataforma como OpenShift |
| 18–43 | **Demo — 25 min** | Inventory → análise → evidência → orientação → regra corporativa |
| 43–50 | Apresentação | Remediação, validação e fluxo do desenvolvedor |
| 50–55 | Apresentação | Escala, governança e ondas de migração |
| 55–60 | Discussão | Perguntas e proposta de piloto |

**Regra de condução:** às 43 min, voltar à apresentação. Não iniciar live coding, deploy, instalação de extensão ou nova análise que exija espera. Se houver atraso, aplicar o corte de 5 minutos indicado abaixo.

## 00–05 · Abertura: por que modernizar?

**Slide 1 — Título e objetivo (00–02).**

Diga:

> “Hoje vamos conectar três decisões: quais aplicações priorizar, o que precisa mudar e como executar essa mudança de forma repetível. O MTA será o apoio para ligar essas decisões às evidências do código.”

**Slide 2 — Modernização e plataforma (02–05).**

Mostre o contraste entre uma aplicação funcionando em um servidor e a mesma aplicação precisando tolerar substituição de instâncias e escala horizontal.

Diga:

> “Uma aplicação pode funcionar bem hoje e depender de disco local, estado em memória ou endereços fixos. Precisamos entender esses acoplamentos antes de decidir como ela vai operar em OpenShift.”
>
> “Modernizar pode envolver replatform ou refatoração seletiva. A escolha por microsserviços só faz sentido quando responde a uma necessidade concreta.”

Faça uma pergunta breve: “Hoje, onde está o maior desafio de vocês: conhecer o portfólio, dimensionar as mudanças ou executá-las?” Limite a conversa inicial a um minuto.

## 05–12 · O papel do MTA

**Slide 3 — Três níveis (05–08).**

| Nível | Pergunta | Evidência e decisão |
|---|---|---|
| Portfólio | O que temos e por onde começamos? | Inventory, contexto, responsáveis e assessment |
| Aplicação | O que precisa ser investigado para o destino escolhido? | Analysis, regras, tecnologias e esforço |
| Código | O que o time deve mudar e validar? | Ocorrências, orientação e backlog de remediação |

Diga:

> “Esses níveis se complementam. Um relatório de código não determina sozinho a prioridade de negócio; um assessment não localiza sozinho cada mudança necessária.”

**Slide 4 — Assessment, analysis e remediation (08–12).**

Mostre: **avaliar a estratégia → identificar mudanças → implementar e validar**.

Diga:

> “No assessment discutimos contexto arquitetural, operacional e de negócio. Na análise procuramos evidências nos artefatos. Na remediação transformamos essas evidências em mudanças testadas.”
>
> “A pontuação de esforço ajuda a comparar trabalho técnico. Para estimar um projeto, precisamos acrescentar integrações, testes, dados, dependências e capacidade da equipe.”

Transição: “Antes de abrir a ferramenta, vamos estabelecer o que queremos observar no código.”

## 12–18 · Boas práticas para OpenShift

**Slide 5 — Estado e persistência (12–15).**

Diga:

> “Quando encontramos um arquivo local, a primeira pergunta é para que ele serve. Log, cache, configuração e documento de negócio têm necessidades diferentes. A solução depende de durabilidade, concorrência e recuperação.”
>
> “Estado de sessão em uma JVM também exige uma decisão quando temos mais de uma instância ou quando o processo reinicia.”

Exemplos: logs para stdout; temporários com ciclo de vida definido; persistência escolhida conforme os requisitos; sessão eliminada quando possível ou tratada por mecanismo apropriado. Não apresente uma correspondência automática ‘arquivo → PVC’ como solução universal.

**Slide 6 — Configuração, integração e runtime (15–18).**

Diga:

> “Queremos configuração que possa mudar sem recompilar, integrações com contratos claros e um runtime compatível com a aplicação. Containerização e migração de Java EE para Jakarta são decisões diferentes.”
>
> “Agora vamos ver como o MTA encontra algumas dessas evidências e como interpretamos os resultados.”

## 18–43 · Demo prática — apenas este bloco é demonstração

Antes da sessão, deixar abertos: [inventário MTA](https://mta-mta.apps.cluster-7vkrl.dyn.redhatworkshops.io/applications), [repositório Gitea](https://gitea.apps.cluster-7vkrl.dyn.redhatworkshops.io/lab-user/sef-mg-mta-demo), [regra corporativa](https://gitea.apps.cluster-7vkrl.dyn.redhatworkshops.io/lab-user/sef-mg-mta-demo/src/branch/main/rules/sef-corporativo/mainframe.yaml) e [resultados reais](results-mta-7.3.2.md).

### 18–22 · Inventory e contexto — 4 min

**Faça:** localize `SEF-MG DEMO - Portal de arrecadacao` e `SEF-MG DEMO - Conciliacao batch`. Abra o cadastro do portal; mostre repositório, branch e caminho do módulo. Mostre que Assessment/Review não foram concluídos, enquanto Analysis está Completed.

**Diga:**

> “São dois exemplos fictícios. Compartilham um repositório, mas têm cadastros e análises próprios. Um representa atendimento; o outro, uma rotina de retaguarda.”
>
> “A análise técnica já foi executada. A avaliação de negócio ainda precisaria ser construída com os responsáveis. Não vamos inventar criticidade ou respostas de assessment para concluir a tela.”

Se abrir o questionário, mostre apenas sua finalidade e uma pergunta pertinente. Não o preencha ao vivo.

### 22–25 · Contexto da aplicação e perfil da análise — 3 min

**Faça:** mostre brevemente o fonte do portal e o perfil usado: Source code, Application and internal dependencies, `cloud-readiness` e `jakarta-ee`. Use o resumo registrado em resultados ou o assistente Analyze, cancelando após Review. Não dispare nova execução no roteiro principal.

**Diga:**

> “O portal emite um recibo fictício, grava em disco e guarda informações na sessão. Os endpoints são exemplos, sem comunicação com sistemas reais.”
>
> “Analisamos duas perguntas: prontidão para containers e mudanças para Jakarta EE. O resultado que vamos abrir é real, produzido pelo MTA 7.3.2 deste cluster.”

O portal local, sua execução e o restart são material opcional de ensaio/perguntas, não uma etapa obrigatória destes três minutos.

### 25–34 · Findings do portal — 9 min

**Faça:** abra a análise concluída. Mostre o total uma única vez: **8 tipos de issues, 14 ocorrências, 16 pontos de esforço**. Percorra só três temas.

| Tempo | Abra | O que dizer |
|---|---|---|
| 25–28 | `local-storage-00001`, PortalServlet.java:45/48 | “Aqui está a escrita local. Para um recibo durável, precisamos definir armazenamento e verificar recuperação após substituição da instância. A ferramenta localiza a evidência; o requisito orienta a solução.” |
| 28–30 | `hardcoded-ip-address`, PortalServlet.java:17 | “Este endereço está embutido no código. A ação proposta é externalizar configuração e revisar como o serviço de destino será identificado e acessado.” |
| 30–34 | `javax-to-jakarta-import-00001`, imports do portal; dependência no POM; web.xml:2 | “A mudança de runtime envolve imports, bibliotecas e descritores. Trocar apenas o pacote não conclui a migração; precisamos implantar e testar no runtime escolhido.” |

Diga ao terminar:

> “Cada finding precisa virar uma ação com responsável e critério de aceite. Esses dezesseis pontos não são dezesseis dias.”

**Precisão:** a sessão em memória existe no exemplo, mas não gerou finding nesta execução. Não a apresente como detecção do MTA.

### 34–39 · Regra corporativa — 5 min

**Faça:** abra `sef-mainframe-direto-00001` no portal, linha 18. Depois mostre o YAML no Gitea: `ruleID`, `message` e `when`. Não explique toda a sintaxe.

**Diga:**

> “Esta política é fictícia: substituir integração direta com um hostname de mainframe por uma API institucional. Não é uma norma real da Secretaria.”
>
> “A regra permite que conhecimento da organização se torne uma verificação reutilizável. Ela encontrou uma ocorrência no portal e uma no batch.”
>
> “Como é uma busca textual, pode encontrar comentários e não encontrar um endereço construído dinamicamente. Precisamos testar e manter as regras.”

Não altere o código nem execute reanálise neste bloco.

### 39–43 · Do resultado ao trabalho do time — 4 min

**Faça:** mostre brevemente o batch: **7 tipos, 9 ocorrências, 15 pontos**. Depois abra o backlog `docs/remediation.md` e escolha um critério de aceite.

**Diga:**

> “O batch compartilha problemas com o portal, mas exige perguntas próprias: pode ser reexecutado? Como evitar duplicação? Qual a janela operacional?”
>
> “Para o portal, um critério seria recuperar o recibo após restart e validar o comportamento com duas réplicas. A próxima etapa é implementar e testar essa decisão.”

**Às 43 min, volte aos slides.** Frase de transição:

> “Vimos como chegar da aplicação à evidência e ao trabalho necessário. Agora vamos ampliar isso para o fluxo da equipe e para o portfólio.”

### Corte para demo de 20 minutos, se necessário

Use 18–21 inventory; 21–23 perfil; 23–30 findings; 30–35 regra corporativa; 35–38 backlog. Corte a navegação pelo batch e o detalhamento de XML. Preserve filesystem, IP e regra corporativa. Use os cinco minutos recuperados para dúvidas ou explicação arquitetural; não para novos cliques.

## 43–50 · Fluxo do desenvolvedor e validação

**Slide 7 — Evidência → mudança → teste → reanálise (43–47).**

Diga:

> “A equipe recebe uma evidência, define uma mudança, testa o comportamento e repete a análise com o mesmo escopo. A redução de findings é um indicador; a validação funcional e operacional completa a avaliação.”
>
> “Algumas mudanças de código são mecânicas e podem ser automatizadas. Decisões sobre estado, dados e contratos exigem desenho e validação.”

Retome a intenção do chat original de aproximar a análise do trabalho no IDE. Nesta sessão, mostre o fluxo conceitual e o código já visto; não prometa uma extensão ou transformação que não foi ensaiada. OpenRewrite, IDE e IA ficam como possibilidades a preparar em uma próxima sessão, após verificar suporte/configuração na versão escolhida.

**Slide 8 — Critérios de aceite para o destino (47–50).**

| Decisão | Evidência de conclusão |
|---|---|
| Configuração externa | Alterar destino sem recompilar; configuração inválida detectada |
| Persistência/estado | Recuperação após restart e comportamento verificado com duas réplicas |
| Runtime | Build, deploy e testes funcionais no runtime compatível |
| Operação | Observabilidade, configuração e comportamento de falha validados |

Diga:

> “O objetivo é operar a aplicação de forma confiável na plataforma de destino. Nenhum desses critérios fica automaticamente comprovado porque o relatório tem menos ocorrências.”

As aplicações desta demo ainda não foram implantadas no OpenShift e não existe versão modernizada pronta.

## 50–55 · Escala e governança

**Slide 9 — Reutilizar padrões e planejar ondas (50–53).**

Mostre a sequência: **inventário → assessment → padrões comuns → análise → decisões → ondas → validação e repetição**.

Retome Archetypes como tema de agrupamento/avaliação de aplicações semelhantes; regras corporativas como verificação compartilhada; migration waves como organização da execução. Não diga que configuramos archetypes ou ondas específicos para a demo: isso não foi feito.

Diga:

> “Quando várias aplicações compartilham padrões, podemos reaproveitar decisões, regras e critérios de teste. A priorização ainda combina esforço técnico, criticidade e dependências.”
>
> “O batch pode ser um candidato a piloto, mas isso é uma hipótese. A janela operacional e as integrações podem mudar a decisão.”

**Slide 10 — Governança e próximos recursos (53–55).**

Diga:

> “As regras precisam ter dono, versão e exemplos de teste. As decisões precisam virar trabalho rastreável, e cada onda precisa de critérios de entrada e saída.”

Integrações com gestão de trabalho, IDE, refactoring e assistência por IA eram temas do chat original. Mencione-os como próximos desdobramentos, sem demo e sem afirmar que estão configurados. A integração com Jira e os componentes de IA precisam de validação própria. Não pressuponha uma PoC de IA na Secretaria só porque ela foi mencionada pela resposta anterior.

## 55–60 · Conclusão e perguntas

**Slide 11 — Proposta de piloto.**

Diga:

> “Modernizar começa entendendo o portfólio, escolhendo a estratégia e transformando conhecimento arquitetural em um processo repetível.”
>
> “A proposta de próximo passo é escolher uma aplicação com responsável disponível, destino definido e condições de teste. Executamos a avaliação, priorizamos mudanças e validamos uma primeira evolução.”

Pergunte: **“Qual aplicação teria hoje contexto e condições de teste suficientes para começar esse piloto?”**

Use o restante do tempo para perguntas. O [guia detalhado](guia-apresentador.md) contém respostas e contingências, mas sua antiga agenda de uma hora de demo está substituída por este documento.

## Material de apoio e limites

- [Guia detalhado de operação, falas e perguntas](guia-apresentador.md): consulta e ensaio; não seguir integralmente em palco.
- [Resultados reais MTA 7.3.2](results-mta-7.3.2.md): números e evidências confirmadas.
- [Backlog de remediação](remediation.md): próximos passos, não mudanças já realizadas.
- [README](../README.md): build e execução local.
- Se o cluster estiver indisponível, usar resultados e capturas do ensaio. Não apresentar expectativa como finding detectado.
- A demo não inclui live coding, restart obrigatório, nova análise com espera, preenchimento completo de assessment, configuração de ondas, IDE, IA ou deploy.
