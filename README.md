# AIAD

## Trabalho Prático 1

O trabalho tem como objectivo criar um sistema para gestão de uso de um conjunto de postos de controlo num aeroporto, com o intuito de minimizar o tempo de espera das pessoas que os utilizarão.



Os postos de controlo são representados por agentes, capazes de comumincar entre si, facilitando a distribuição de tarefas - verificação das malas de cada pessoa - e têm uma capacidade limite determinada pelo número de malas de porão. Assume-se que duas malas de cabine correspondem a uma mala de porão e que o tempo de verificação de uma mala é constante e proporcional ao tamanho. 

(editar)
Assim, para comunicação, deverão ser utilizados, maioritariamente, os protocolos FIPA Request e/ou Contract-Net. As tarefas são acontecimentos estocásticos e a sua ocorrência pode ser parametrizada (para refletir, por exemplo, diferentes padrões de pedidos característicos de diferentes alturas de um dia).

(editar)
O sistema será desenvolvido para suportar diferentes cenários, nomeadamente o botão de chamada em cada piso apenas indicar a direção desejada (subir/descer) vs indicar o piso de destino. Se possível, serão também exploradas outras otimizações, como elevadores parados (sem tarefas para executar) se posicionarem num piso de espera ótimo (minimizando tempos de viagem futuros).

Variáveis dependentes:

- Tempos de espera
- Taxa de ocupação
- Numero de malas verificadas por posto

Variáveis independentes:

- Número de postos de controlo
- Capacidade máxima de cada posto
- Tempo de verificação de uma mala

