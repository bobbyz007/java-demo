package com.example.ddd.mapstruct;

public class CarMapperTest {
    public static void main(String[] args) {
        //given
        Car car = new Car( "Morris", 5, CarType.SEDAN );

        CarDto carDto = CarMapper.INSTANCE.toCarDto(car);

        System.out.println(carDto);
    }
}
