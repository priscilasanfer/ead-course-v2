# EAD Course V2

### Anotações

- *@EntityGraph(attributePaths = {"course"})*: Alterna de lazy para eager ou seja é uma feature para consultas de subrecursos 
de forma a forçar o carregamento eager de entidades relacionadas e ainda evitando o problema de N+1 e não substitui o jpql
- *@Fetch(FetchMode.SUBSELECT)* = Faz uma consulta para o curso e uma consulta para os modulos desse curso respeitando o FetchType
- *@Modifying + @Query* = Quando precisa deletar ou atualizar um dado de forma customizada 
- *cascade = CascadeType.ALL* : Gera um sql para deletar a entidade e mais um sql de deleção para cada relacionamento vinculado a entidade. Podendo gerar um gargalo.
- *orphanRemoval = true*: cada relacionamento precisa estar vinculado a uma entidade se tiver algum relaciomento que não tenha um vinculo com a entidade ele também vai ser deletado
- *@OnDelete()*: Um pouco melhor que o cascade = CascadeType.ALL com orphanRemoval = true. Usando a action OnDeleteAction.CASCADE estamos delegando para o banco de dados
- *@Transactional*: Faz tudo dentro de uma transação no banco e se algo der errado ele volta para o estado anterior sem afetar os dados no banco 


#### Executar o projeto

- Rodar o projeto: https://github.com/priscilasanfer/service-registry
- Rodar o projeto: https://github.com/priscilasanfer/api-gateway
- Rodar o projeto: https://github.com/priscilasanfer/ead-authuser
- Rodar a aplicação

#### Como abrir diagrama do projeto:
- Acessar o site: https://app.diagrams.net/
- Fazer o upload do arquivo: src/main/resources/files/EAD-Arquitetura-Microservices.drawio

### Versão 2 
- utiliza comunicação sincrona e assincrona entre os microservices

### RabbitMq
- https://customer.cloudamqp.com/instance
- 