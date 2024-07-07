# Справочное описание методов для тестирования API
## 1.1 Регистрация пользователя

**Эндпоинт:** `/user/register`  
**Метод:** `POST`  
**Описание:** Регистрирует нового пользователя.

**Параметры запроса:**

- **Content-Type:** `application/json`
- **Тело запроса:** JSON, представляющий объект `UserDTO`.

```
{
  "username": "exampleUser",
  "password": "examplePassword"
}
```
**Ответы**

- **200 OK**: Успешная регистрация пользователя.

```
  "User 'exampleUser' successfully registered"
```

- **409 CONFLICT**: Ошибка при регистрации пользователя.

```
  "error": "The operation was not completed. Please, check logs"
```

## 1.2. Вход пользователя

**Эндпоинт:** `/user/login`  
**Метод:** `POST`  
**Описание:** Авторизация пользователя.

**Параметры запроса:**

- **Content-Type:** `application/json`
- **Тело запроса:** JSON, представляющий объект `UserDTO`.

```
{
  "username": "exampleUser",
  "password": "examplePassword"
}
```
**Ответы**

- **200 OK**: Успешный вход пользователя.

```
  "User 'exampleUser' successfully logged in"
```

- **409 CONFLICT**: Ошибка при входе пользователя.

```
  "error": "The operation was not completed. Please, check logs"
```


## 2.1. Создание комнаты

**Эндпоинт:** `/room`  
**Метод:** `POST`  
**Описание:** Создает новую комнату.

**Параметры запроса:**

- **Content-Type:** `application/json`
- **Тело запроса:** JSON, представляющий объект `RoomDTO`.

```
{
  "name": "exampleRoom",
  "type": "exampleType"
}
```

**Ответы**

- **200 OK**: Успешное создание комнаты.

```
{
  "name": "exampleRoom",
  "type": "exampleType"
}
```

- **409 CONFLICT**: Ошибка при создании комнаты.

```
{
  "error": "The room hasn't been created. Please, check logs"
}
```

- **401 UNAUTHORIZED**:  Требуется авторизация.

```
{
  "error": "Requires logging in first"
}
```

- **400 BAD REQUEST**: Ошибка валидации.

```
{
  "error": "Validation error message"
}
```

- **500 INTERNAL SERVER ERROR**: Внутренняя ошибка сервера.

```
{
  "error": "Internal server error message"
}
```

## 2.2. Получение списка комнат

**Эндпоинт:** `/room`  
**Метод:** `GET`  
**Описание:** Возвращает список всех комнат.

**Ответы**

- **200 OK**: Успешное получение списка комнат.

```
[
  {
    "name": "exampleRoom",
    "type": "exampleType"
  }
]
```

- **404 NOT FOUND**: Комнаты не найдены.

```
{
  "error": "Rooms not found"
}
```

- **401 UNAUTHORIZED**:  Требуется авторизация.

```
{
  "error": "Requires logging in first"
}
```

- **400 BAD REQUEST**: Ошибка валидации.

```
{
  "error": "Validation error message"
}
```

- **500 INTERNAL SERVER ERROR**: Внутренняя ошибка сервера.

```
{
  "error": "Internal server error message"
}
```

## 2.3. Обновление комнаты

**Эндпоинт:** `/room`  
**Метод:** `PUT`  
**Описание:** Обновляет данные существующей комнаты.

**Параметры запроса:**

- **Content-Type:** `application/json`
- **Тело запроса:** JSON, представляющий объект UpdateRoomRequest.

```
{
  "originalRoomName": "oldRoomName",
  "newRoomName": "newRoomName",
  "newRoomType": "newRoomType"
}
```

**Ответы**

- **200 OK**: Успешное обновление комнаты.

```
Room updated
```

- **409 CONFLICT**: Ошибка при обновлении комнаты.

```
{
  "error": "Incorrect input data. Please, check logs"
}
```

- **401 UNAUTHORIZED**:  Требуется авторизация.

```
{
  "error": "Requires logging in first"
}
```

- **400 BAD REQUEST**: Ошибка валидации.

```
{
  "error": "Validation error message"
}
```

- **500 INTERNAL SERVER ERROR**: Внутренняя ошибка сервера.

```
{
  "error": "Internal server error message"
}
```

## 2.4. Удаление комнаты

**Эндпоинт:** `/room`  
**Метод:** `DELETE`  
**Описание:** Удаляет комнату.

**Параметры запроса:**

- **Параметры URL:** roomName - имя комнаты для удаления.

**Ответы**

- **200 OK**:  Успешное удаление комнаты.

```
Room deleted
```

- **409 CONFLICT**: Ошибка при удалении комнаты.

```
{
  "error": "The operation was not completed. Please, check logs"
}
```

- **401 UNAUTHORIZED**:  Требуется авторизация.

```
{
  "error": "Requires logging in first"
}
```

- **400 BAD REQUEST**: Ошибка валидации.

```
{
  "error": "Validation error message"
}
```

- **500 INTERNAL SERVER ERROR**: Внутренняя ошибка сервера.

```
{
  "error": "Internal server error message"
}
```

## 3.1. Создание бронирования

**Эндпоинт:** `/booking`  
**Метод:** `POST`  
**Описание:** Создает новое бронирование.

**Параметры запроса:**

- **Content-Type:** `application/json`
- **Тело запроса:** JSON, представляющий объект `BookingDTO`

> поле username будет получено из параметров сессии

```
{
  "roomName": "exampleRoom",
  "startTime": "2023-01-01T10:00:00",
  "endTime": "2023-01-01T11:00:00"
}
```

**Ответы**

- **201 CREATED**:  Успешное создание бронирования.

```
{
  "username": "loggedUser",
  "roomName": "exampleRoom",
  "startTime": "2023-01-01T10:00:00",
  "endTime": "2023-01-01T11:00:00"
}
```

- **409 CONFLICT**: Ошибка при создании бронирования.

```
{
  "error": "This time is not available for bookings. Try another time."
}
```

- **401 UNAUTHORIZED**:  Требуется авторизация.

```
{
  "error": "Requires logging in first"
}
```

- **400 BAD REQUEST**: Ошибка валидации.

```
{
  "error": "Validation error message"
}
```

- **500 INTERNAL SERVER ERROR**: Внутренняя ошибка сервера.

```
{
  "error": "Internal server error message"
}
```

## 3.2. Получение доступных часов для бронирования

**Эндпоинт:** `/booking/available-hours`  
**Метод:** `GET `  
**Описание:** Возвращает список доступных часов для бронирования на определенную дату и комнату.

**Параметры запроса:**

- **Content-Type:** `application/json`
- **Тело запроса:** JSON, представляющий объект GetAvailableHoursRequest.


```
{
  "date": "2023-01-01",
  "roomName": "exampleRoom"
}
```

**Ответы**

- **200 OK**:  Успешное получение доступных часов.

```
{
  [
  "10:00:00",
  "11:00:00"
]
}
```

- **404 NOT FOUND**: Доступные часы не найдены.

```
{
  "error": "No available hours found for the given date and room."
}
```


- **400 BAD REQUEST**: Ошибка валидации.

```
{
  "error": "Validation error message"
}
```

- **500 INTERNAL SERVER ERROR**: Внутренняя ошибка сервера.

```
{
  "error": "Internal server error message"
}
```

## 3.3. Фильтрация бронирований

**Эндпоинт:** `/booking/filter `  
**Метод:** `GET `  
**Описание:** Возвращает список бронирований на основе фильтров (дата, имя пользователя, имя комнаты).

**Параметры запроса:**

- **Content-Type:** `application/json`
- **Тело запроса:** JSON, представляющий объект FilterBookingsRequest.

> В запрос должен передаться только один параметр, по которому выполнится фильтрация


```
{
  "date": "2023-01-01"
}
```
```
{
  "username": "exampleUser"
}
```
```
{
  "roomName": "exampleRoom"
}
```

**Ответы**

- **200 OK**:  Успешное получение списка бронирований.

```
[
  {
    "username": "exampleUser",
    "roomName": "exampleRoom",
    "startTime": "2023-01-01T10:00:00",
    "endTime": "2023-01-01T11:00:00"
  }
]
```

- **404 NOT FOUND**: Бронирования не найдены.

```
{
  "error": "No results were found for your selected filter parameters."
}
```


- **400 BAD REQUEST**: Ошибка валидации.

```
{
  "error": "Validation error message"
}
```

- **500 INTERNAL SERVER ERROR**: Внутренняя ошибка сервера.

```
{
  "error": "Internal server error message"
}
```

## 3.4. Обновление бронирования

**Эндпоинт:** `/booking`  
**Метод:** `PUT `  
**Описание:** Обновляет данные существующего бронирования.

**Параметры запроса:**

- **Content-Type:** `application/json`
- **Тело запроса:** JSON, представляющий объект UpdateBookingRequest.

```
{
  "originalRoomName": "oldRoomName",
  "originalStartTime": "2023-01-01T10:00:00",
  "newRoomName": "newRoomName",
  "newStartTime": "2023-01-01T11:00:00",
  "newEndTime": "2023-01-01T12:00:00"
}
```

**Ответы**

- **200 OK**:  Успешное обновление бронирования.

```
Booking updated
```

- **409 CONFLICT**: Ошибка при обновлении бронирования.

```
{
  "error": "The operation was not completed. Please, check logs"
}
```

- **401 UNAUTHORIZED**:  Требуется авторизация.

```
{
  "error": "Requires logging in first"
}
```


- **400 BAD REQUEST**: Ошибка валидации.

```
{
  "error": "Validation error message"
}
```

- **500 INTERNAL SERVER ERROR**: Внутренняя ошибка сервера.

```
{
  "error": "Internal server error message"
}
```

## 3.5. Удаление бронирования

**Эндпоинт:** `/booking`  
**Метод:** `DELETE `  
**Описание:**  Удаляет бронирование.

**Параметры запроса:**

- **Content-Type:** `application/json`
- **Тело запроса:** JSON, представляющий объект BookingDTO

```
{
  "roomName": "exampleRoom",
  "startTime": "2023-01-01T10:00:00",
}
```

**Ответы**

- **200 OK**:  Успешное удаление бронирования.

```
Booking deleted
```

- **409 CONFLICT**: Ошибка при удалении бронирования.

```
{
  "error": "The operation was not completed. Please, check logs"
}
```

- **401 UNAUTHORIZED**:  Требуется авторизация.

```
{
  "error": "Requires logging in first"
}
```


- **400 BAD REQUEST**: Ошибка валидации.

```
{
  "error": "Validation error message"
}
```

- **500 INTERNAL SERVER ERROR**: Внутренняя ошибка сервера.

```
{
  "error": "Internal server error message"
}
```