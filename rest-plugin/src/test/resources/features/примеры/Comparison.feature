#language:en
@api_examples
@comparison
Feature: comparison

  Scenario: generate variable and comparison
    * create context variables
      | one               | 1    |
      | процентная ставка | 0.05 |

    #первый столбец не несет никакой логики, нужен для упрощения восприятия теста
    #кратко указать что именно подразумевает сравнение
    * perform a math operation and save result to the variable
      | operation_alias | values_type | first_value | operation              | variable_name |
      | Просто так      | int         | 2           | +%{one}%               | new_three     |
      | Проценты        | double      | 100         | *%{процентная ставка}% | new           |

    #можно указать формат, примеры здесь: https://www.baeldung.com/java-decimalformat
    #округление выключено
    #можно посмотреть в логах какие переменные сохраняются в каждом случае
    * perform a math operation and save result to the variable
      | operation_alias               | values_type | first_value   | operation   | variable_name     | format     |
      | сложение_нецелой_суммы_format | double      | %{new_three}% | +0.55555555 | new_three_decimal | #          |
      | сложение_нецелой_суммы_format | double      | %{new_three}% | +0.55555555 | new_three_decimal | #.##       |
      | сложение_нецелой_суммы_format | double      | %{new_three}% | +0.55555555 | new_three_decimal | #,##       |
      | сложение_нецелой_суммы_format | double      | %{new_three}% | +0.55555555 | new_three_decimal | #.00       |
      | сложение_нецелой_суммы_format | double      | %{new_three}% | +0.55555555 | new_three_decimal | 00.0000000 |
      #можно не писать формат в строчке, если не нужен
      | сложение_нецелой_суммы_format | double      | 3.55555554    | +0.55555555 | new_three_decimal |            |