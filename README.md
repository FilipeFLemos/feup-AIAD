# feup-AIAD

## Trabalho Prático 1

O trabalho tem como objectivo criar um sistema para gestão de uso de um conjunto de postos de controlo num aeroporto, com o intuito de minimizar o tempo de espera das pessoas que os utilizarão.

O sistema será composto por 4 tipos de agentes: 1) Gestor de Fila, 2) Controlo de Mala, 3) Controlo de Pessoas e 4) Inspetor de Irregularidades. 

O Agente Gestor de Fila verificará a pessoa que se encontra à frente da fila e pode exibir dois comportamentos diferentes: 

- Caso a pessoa em questão possua pelo menos uma mala, questiona os Controlo de Mala para saber qual deles está mais livre (taxa de ocupação do tapete mais reduzida) e, sucessivamente, envia a pessoa para esse posto. A resposta dos agentes de Controlo de Mala ao Gestor de Fila inclui unicamente o tamanho ocupado pelas malas no seu tapete. 

- Caso a pessoa em questão não possua nenhuma mala, questiona os Controlo de Pessoas para saber qual deles está mais livre (menor número de pessoas na fila) e, sucessivamente, envia a pessoa para esse posto. A resposta dos agentes de Controlo de Pessoas ao Gestor de Fila inclui unicamente o número de pessoas na fila. 

 O Agente Controlo de Mala, introduz um tempo constante e proporcional ao tamanho da mala (assume-se que uma mala de porão equivale a 2 malas de cabine). Para além disso, durante o processo de verificação da mala, poderá demonstrar dois comportamentos: 
 
- Caso haja alguma irregularidade na mala, solicita-se o Inspector de Irregularidades que se encontra mais próximo.
 
- Caso não haja qualquer irregularidade na mala, questiona os Controlos de Pessoas para saber qual deles está mais livre (menor número de pessoas na fila) e, sucessivamente, envia a pessoa para esse posto. 

 O Agente Controlo de Pessoas limita-se a responder aos pedidos feitos pelos outros agentes e introduz um tempo constante ao tempo total de espera de uma pessoa.
 
 Por fim, o Agente Inspetor de Irregularidades tem como função verificar manualmente as malas, exporadicamente solicitadas pelos postos de Controlo de Mala. Como se trata de um serviço exporádico, assume-se que pode andar por qualquer ponto do aeroporto, sendo necessário calcular o que se encontra mais perto do pedido solicitado. O tempo de verificação manual, introduz um tempo constante e directamente proporcional ao número de malas a ser verificadas ao tempo de espera de uma pessoa.

