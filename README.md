# AIAD

## Trabalho Prático 1

O trabalho tem como objectivo criar um sistema para gestão de uso de um conjunto de postos de controlo num aeroporto, com o intuito de minimizar o tempo de espera das pessoas que os utilizarão.

Temos 4 tipo de agentes, Gestor de Fila, Controlo de Mala, Controlo de Pessoas e Inspetor de Irregularidades.

O Gestor de Fila verifica a pessoa que está na fila resultado em dois comportamentos:
 * caso a pessoa em questão possua mala, o Gestor questiona os Controlo de Mala para saber qual está mais livre para enviar a pessoa
 * caso a pessoa em questão não possua mala, o Gestor questiona os Controlos de Pessoa para saber qual está mais livre para enviar a pessoa
 
 O Controlo de Mala tem dois comportamentos na verificação da mala:
 * caso haja irregularidade na mala, solicita-se o Inspector de Irregularidades mais próximo
 * caso não haja irregularidade na mala, o Controlo de Mala questiona os Controlos de Pessoas para saber qual está mais livre para enviar a pessoa

  O Controlo de Pessoas verifica a pessoa, caso haja irregularidade solicia-se o Inspector de Irregularidades mais próximo
  
  O Inspetor de Irregularidades tem dois comportamentos:
  * caso a pessoa tenha sido enviada pelo Controlo de Mala, verifica a mala manualmente, caso não haja irregularidade, questiona os Controlo de Pessoas para saber qual está mais livre para enviar a pessoa
  * caso a pessoa tenha sido enviada pelo Controlo de Pessoas, verifica a pessoa

