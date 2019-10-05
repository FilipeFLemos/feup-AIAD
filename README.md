# AIAD

## Trabalho Prático 1

O trabalho tem como objectivo criar um sistema para gestão de uso de um conjunto de postos de controlo num aeroporto, com o intuito de minimizar o tempo de espera das pessoas que os utilizarão.

O sistema será composto por 4 tipos de agentes: 1) Gestor de Fila, 2) Controlo de Mala, 3) Controlo de Pessoas e 4) Inspetor de Irregularidades. 

O Gestor de Fila verificará a pessoa que se encontra à frente da fila e pode exibir dois comportamentos diferentes: 

- Caso a pessoa em questão possua pelo menos uma mala, questiona os Controlo de Mala para saber qual deles está mais livre (taxa de ocupação do tapete mais reduzida) e, sucessivamente, envia a pessoa para esse posto. A resposta dos agentes de Controlo de Mala ao Gestor de Fila inclui unicamente o tamanho ocupado pelas malas no seu tapete. 

- Caso a pessoa em questão não possua nenhuma mala, questiona os Controlo de Pessoas para saber qual deles está mais livre (menor número de pessoas na fila) e, sucessivamente, envia a pessoa para esse posto. A resposta dos agentes de Controlo de Pessoas ao Gestor de Fila inclui unicamente o número de pessoas na fila. 

 
 O Controlo de Mala tem dois comportamentos na verificação da mala:
 * caso haja irregularidade na mala, solicita-se o Inspector de Irregularidades mais próximo
 * caso não haja irregularidade na mala, o Controlo de Mala questiona os Controlos de Pessoas para saber qual está mais livre para enviar a pessoa

  O Controlo de Pessoas verifica a pessoa, caso haja irregularidade solicia-se o Inspector de Irregularidades mais próximo
  
  O Inspetor de Irregularidades tem dois comportamentos:
  * caso a pessoa tenha sido enviada pelo Controlo de Mala, verifica a mala manualmente, caso não haja irregularidade, questiona os Controlo de Pessoas para saber qual está mais livre para enviar a pessoa
  * caso a pessoa tenha sido enviada pelo Controlo de Pessoas, verifica a pessoa

