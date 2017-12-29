# Cucumber as rule engine
# Использование cucumber в качестве движка бизнес правил

В данной статье пойдет речь о создании прототипа приложения для проверки идеи использования **cucumber** в качестве движка бизнес-правил. 
Два вопроса, на которые я бы хотел здесь ответить: "зачем?" и "как?".

```
Cucumber is a tool that supports Behaviour-Driven Development (BDD) - a software development process that aims to enhance software quality and reduce maintenance costs.
Gherkin is a Business Readable, Domain Specific Language that lets you describe software's behaviour without detailing how that behaviour is implemented (https://github.com/cucumber/cucumber/wiki/Gherkin).
```

## Зачем
### Причина 1
<kbd>TODO</kbd>
### Причина 2
Во многих проектах, где я принимаю участие, используется drools. Все это покрыто unit тестами, а кое где даже используются BDD тесты. В одном из таких проектах я заметил странную штуку - код BDD тестов сильно походит код drools правил. 
Приведу пример правила из сервис распределения клиентов по группам в зависимости от различных факторов. Код на drools выглядит так:
```drools
rule "For client from country ENG group will be England"
when
    Client(country == "ENG")
then
    insert(new Group("England"));
end
```
Тест выглядит так:
```gherkin
  Scenario: For client from country ENG group will be England
     When client country is "ENG"
     Then group will be "England"
```
Если абстрагироваться от синтаксиса - один в один! Пример по большей части синтетический, но тем не менее он честный - в реальном проекте все так и сложилось. При этом gherkin явно человекопонятнее, так почему бы сразу не писать на нем?
__Проблема__: а чем тогда это тестировать ? drools ? :)

### Причина 3
Предоставить бизнесу простой и понятный инструмент, позволяющий описывать свои намерения/требования к функционалу с помощью бизнес правил и применять их в режиме реального времени или хотя бы "быстрее чем обычно".
Как упомянул выше, мы пользуем drools и любим его (хотя это наверное уже не те романтические чувства, а просто привычка). И для описанной выше задачи в экосистеме drools имеется инструмент - __drools workbench__, который обладает богатым функционалом. Но писать правила на drools совсем не просто и обмазывать их тестами совершенно обязательно. 
__Проблема__: конечно можно дать бизнесу возможность модифицировать правила на живую, но как проверить правильность таких правок ? Бизнес будет писать тесты ? 

## Как
**Сucumber** не заточен под использования вне тестовой среды, но уговорами и угрозами удалось заставить его работать - на костылях, зато взлетел. Оказалось, что эти полдела самая простая часть. Сложные полдела - это придумать, как это можно проверить правильность написанных правил.

Принцип тестирования схож с методом тестирования, когда фигачат разные варианты 
### Формирование датасета
Фомиррование датасета, если через все возможные варианты фактов - очень долго. Как оптимизировать ?

Если взять условие
client's country is 'RUS'
то я бы написал тест для таких значений страны
- null
- RUS
- foo (not RUS)

Если взять условие
client's country is 'RUS'
client's country is 'CHL'
то я бы написал тест для таких значений страны
- null
- RUS
- CHL
- foo (not RUS)

Если взять условие
client's payment > '1000'
то я бы написал тест для таких значений
- null
- 0
- 999
- 1000
- 1001
- -1000

В целом, думаю логика понятна. Таким образом можно сформировать необходимый и достаточный датасет для теста.

В силу того, что цель предоставить бизнесу имеено готовый язык, то программисту придется заранее подготовить логику распарсивания gerkin конструкций, следовательно он может подготовить и датасет!
Т.е. помимо предоставления дифинишн файла реализовать и датасетформироватор.

----
```
Feature: Select group

  Scenario: England
     When client country is "ENG"
     Then group will be "England "

  Scenario: Chile
     When client country is "CHL"
     Then group will be "Chile"

  Scenario: Russia
     When client country is "RUS"
     Then group will be "Russia"

  Scenario: Default group will be chosen for RUS
     When client country is "RUS"
     When client language is not "eng"
     Then group will be "Russia"

  Scenario: Default group will be chosen for RUS
     When client country is "RUS"
     When client language is not "eng"
     And deposit more than 1000
     Then group will be "RichRussia"

  Scenario: Experiment group will be chosen for CHL
     When client country is "RUS"
     When client language is "eng"
     Then group will be "EnRussia"
```

```
Feature: Select group

  Scenario: England
     When client country is "ENG"
     Then group will be "England "

  Scenario: Chile
     When client country is "CHL"
     Then group will be "Chile"

  Scenario: Russia
     When client country is "RUS"
     Then group will be "Russia"

  Scenario: Client country is not defined
     When client country is not defined
     Then group will be "Default"

  Scenario: Default group will be chosen for RUS
     When client country is "RUS"
     When client language is not "eng"
     Then group will be "Russia"

  Scenario: Default group will be chosen for RUS
     When client country is "RUS"
     When client language is not "eng"
     And deposit more than 1000
     Then group will be "RichRussia"

  Scenario: Experiment group will be chosen for CHL
     When client country is "RUS"
     When client language is "eng"
     Then group will be "EnRussia"
```

Допустим есть такие правила
```
Feature: Select group

  Scenario: England
     When client country is "ENG"
     Then group will be "England "

  Scenario: Chile
     When client country is "CHL"
     Then group will be "Chile"
```
Анализ говорит нам, что 
```
Some entries have not been distributed  show
count	entries
2	
#0 {"client":{},"deposit":{}}
#1 {"client":{"country":"any"},"deposit":{}}
```
В данной ситуации мы можем сделать следующее
```
Feature: Select group

  Scenario: England
     When client country is "ENG"
     Then group will be "England "

  Scenario: Chile
     When client country is "CHL"
     Then group will be "Chile"

  Scenario: Default
     When client country no in
     |ENG|
     |CHL|
     Then group will be "Default"
```