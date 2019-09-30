# AIAD

## Trabalho Prático 1

O trabalho tem como objectivo criar um sistema para gestão de uso de um conjunto de postos de controlo num aeroporto, com o intuito de minimizar o tempo de espera das pessoas que os utilizarão.


(editar)
Os elevadores são representados por agentes que podem eventualmente comunicar entre si de forma a facilitar a delegação de tarefas (transporte de uma pessoa) e têm uma capacidade limite, bem como um tempo de transporte entre pisos fixo. Assim, para comunicação, deverão ser utilizados, maioritariamente, os protocolos FIPA Request e/ou Contract-Net. As tarefas são acontecimentos estocásticos e a sua ocorrência pode ser parametrizada (para refletir, por exemplo, diferentes padrões de pedidos característicos de diferentes alturas de um dia).

(editar)
O sistema será desenvolvido para suportar diferentes cenários, nomeadamente o botão de chamada em cada piso apenas indicar a direção desejada (subir/descer) vs indicar o piso de destino. Se possível, serão também exploradas outras otimizações, como elevadores parados (sem tarefas para executar) se posicionarem num piso de espera ótimo (minimizando tempos de viagem futuros).

(editar)
Variáveis independentes:
Número de pisos no edifício
Número de elevadores
Capacidade máxima de cada elevador
Tempo de transporte entre pisos
Probabilidade/Frequência de chamadas em cada piso
Tipo de chamada inicial ao elevador (subir/descer ou piso destino conhecido a priori)
Variáveis dependentes:
Taxa ocupação/elevador
Taxa uso (tarefas executadas)/elevador
Tempo espera mínimo,médio,máximo/pedido
