package FinanceTracker.com.demo.mapper;

import org.modelmapper.ModelMapper;

public abstract class AbstractMapper <Entity,Dto>{
    private Class<Entity> entityClass;

    private Class<Dto> dtoClass;

    private ModelMapper modelMapper;
}
