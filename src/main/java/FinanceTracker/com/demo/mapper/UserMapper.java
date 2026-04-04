package FinanceTracker.com.demo.mapper;

import FinanceTracker.com.demo.dto.UserCreationDto;
import FinanceTracker.com.demo.dto.UserResponseDto;
import FinanceTracker.com.demo.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper  {
    public User toEntity(UserCreationDto userCreationDto) {
        if(userCreationDto==null){
            return null;
        }
          User user = new User();

        user.setEmail(userCreationDto.getEmail());
        user.setFullName(userCreationDto.getFullName());
        user.setIs_admin(userCreationDto.getIs_admin());
        return user;
    }

    public UserResponseDto toResponseDto (User savedUser) {
        if(savedUser == null){
            return null;
        }
        UserResponseDto dto = new UserResponseDto();
        dto.setUsername(savedUser.getUsername());
        dto.setEmail(savedUser.getEmail());
        dto.setFullName(savedUser.getFullName());
        dto.setIs_admin(savedUser.getIs_admin());
        dto.setId(savedUser.getId());
        dto.setCreated_at(savedUser.getCreated_at());
        dto.setUpdated_at(savedUser.getUpdated_at());


        return dto;
    }
}
