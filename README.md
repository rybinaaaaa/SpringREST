# # #Spring/rest

## 0. Intro 

> **REST API** используется в микросервисной архитектуре. Это вид коммуникации с сервером, в котором подразумевается 
> **Микросервисная архитектура** - приложение, которое состоит из микро приложений, которые выполняют относительно элементарные действия (**микросервисы **)


![](Screenshot%202023-10-25%20at%2020.34.29.png)<!-- {"width":573} -->

```
        RestTemplate restTemplate = new RestTemplate();


//        ---------------------------------------------------------------
//        Get request

//        String url = "https://reqres.in/api/users/2";
//        String response = restTemplate.getForObject(url, String.class);

--------------- Если нам нужен список объектов

List<MeasurementDTO> response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<MeasurementDTO>>() {
        }
).getBody();
----------------
//        ---------------------------------------------------------------

//        Post request

//        Map<String, String> jsonToSend = new HashMap<>();
//        jsonToSend.put("name", "testName");
//        jsonToSend.put("job", "testJob");
//
//        HttpEntity<Map<String, String>> request = new HttpEntity<>(jsonToSend);
//
//        String url = "https://reqres.in/api/users/";
//        String response = restTemplate.postForObject(url, request,String.class);

//        ---------------------------------------------------------------

//        System.out.println(response);

```

*пример исплользвания*

## 1. Get запросы
```
@GetMapping("/{id}")
public PersonDTO getPerson(@PathVariable("id") int id) {
    return convertToPersonDTO(peopleService.findOne(id)); // Jackson конвертирует в JSON
}
```

— в спрингбут автоматически встроен Jackson - он стерилизует данные в json, то есть наш объект на выходе будет автоматически отправлен клиенту как json!!!
— **для этого сы должны поменять аннотацию @Controller на @RestController**
## 2. Exception Handler

> Если в мы сделаем запрос к несуществующему айдишнику, то нам сервер ничего не вернет, только пустую строку

Для того, чтобы этого не было:

1. Создаем класс ошибки и ответ на это ошибку:

*класс ошибки*
```
public class PersonNotFoundException extends RuntimeException {
}
```

*класс ответа на ошибку*
```
public class PersonErrorResponse {
    private String message;
    private long timestamp;

    public PersonErrorResponse(String message, long timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
```

2. Кидаем ошибку вместо того, чтобы возвращать null
```
PeopleService:

public Person findOne(int id) {
    Optional<Person> foundPerson = peopleRepository.findById(id);
    return foundPerson.orElseThrow(PersonNotFoundException::new);
}
```

3. Обрабатываем ошибку:
```
PeopleController:

@ExceptionHandler
private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException e) {
    PersonErrorResponse response = new PersonErrorResponse("Person with this id wasn't found",
            System.currentTimeMillis());
    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
}
```

- Добавляем аннотацию **@ExceptionHandler**
- Метод возвращает **ResponseEntity<PersonErrorResponse>**, указываем в дженерики, что ловим именно  **PersonErrorResponse**
- Создаем внутри  **PersonErrorResponse** и вкладываем его в **ResponseEntity** со статусом **HttpStatus.NOT_FOUND**
## 3. Post request

```
@PostMapping
public ResponseEntity<HttpStatus> save(@RequestBody @Valid MeasurenentDTO measurenentDTO, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
        StringBuilder error = new StringBuilder();
        for (FieldError e : bindingResult.getFieldErrors()) {
            error.append(e.getField())
                    .append(" - ")
                    .append(e.getDefaultMessage())
                    .append(";");
        }
        throw new MeasurementNotCreatedException(error.toString());
    }
    measurementService.save(MeasurenentDTO.convertFromDTO(measurenentDTO));

    return ResponseEntity.ok(HttpStatus.OK);
}
```

## 4. DTO

> **DTO**(data transfer object) - это прослойка между клиентом и моделью.

**Модель** - бизнес логика
**DTO** - объект для передачи данных

### Базовая имплементация: 

```
package com.rybina.firstRestAPI.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class PersonDTO {

    @NotEmpty(message = "name should nit be empty")
    @Size(min = 2, max = 5, message = "name should be between 2 and 5 letters")
    private String name;

    @Min(value = 0, message = "age should be greater than zero")
    private int age;

    @Email
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

// PersonMapper
    public Person convertToPerson(PersonDTO p) {
        Person person = new Person();
        person.setName(p.getName());
        person.setAge(p.getAge());
        person.setEmail(p.getEmail());
        return person;
    }
}
```

### ModelMapper

— можно установить библиотеку, которая сама будет маппить

```
        <dependency>
            <groupId>org.modelmapper</groupId>
            <artifactId>modelmapper</artifactId>
            <version>3.1.1</version>
        </dependency>
```

```
@Bean public ModelMapper modelMapper() { return new ModelMapper(); }
```

Использование 

```
    private PersonDTO convertToPersonDTO(Person p) {
        return modelMapper.map( p, PersonDTO.class);
    }
```
#Spring
