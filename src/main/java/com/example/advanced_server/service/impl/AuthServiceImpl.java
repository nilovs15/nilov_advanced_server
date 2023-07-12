package com.example.advanced_server.service.impl;

import com.example.advanced_server.dto.AuthDTO;
import com.example.advanced_server.dto.CustomSuccessResponse;
import com.example.advanced_server.dto.LoginUserDto;
import com.example.advanced_server.dto.RegisterUserDTO;
import com.example.advanced_server.entity.UserEntity;
import com.example.advanced_server.exception.CustomException;
import com.example.advanced_server.exception.ValidationConstants;
import com.example.advanced_server.mappers.LoginUserDtoMapper;
import com.example.advanced_server.mappers.UserEntityMapper;
import com.example.advanced_server.repository.UserRepository;
import com.example.advanced_server.security.JwtTokenProvider;
import com.example.advanced_server.service.AuthService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public CustomSuccessResponse<LoginUserDto> register(RegisterUserDTO registerUser) {
        if (userRepository.findByEmail(registerUser.getEmail()) != null) {
            throw new CustomException(ValidationConstants.USER_ALREADY_EXISTS);
        }
            UserEntity userEntity = UserEntityMapper.INSTANCE.registerUserDtoToUserEntity(registerUser);
            userEntity.setPassword(passwordEncoder.encode(registerUser.getPassword()));
            userRepository.save(userEntity);
            LoginUserDto loginUserDto = LoginUserDtoMapper.INSTANCE.userEntityToLoginUserDTO(userEntity);
            loginUserDto.setToken(jwtTokenProvider.createToken(registerUser.getEmail()));
            return CustomSuccessResponse.getResponse(loginUserDto);
    }

    public CustomSuccessResponse<LoginUserDto> login(AuthDTO authDTO) {
        try {
            UserEntity user = userRepository.findByEmail(authDTO.getEmail());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), authDTO.getPassword()));
            LoginUserDto loginUserDto = LoginUserDtoMapper.INSTANCE.userEntityToLoginUserDTO(user);
            loginUserDto.setToken(jwtTokenProvider.createToken(authDTO.getEmail()));
            return CustomSuccessResponse.getResponse(loginUserDto);
        }
        catch (Exception e) {
            throw new CustomException(ValidationConstants.USER_NOT_FOUND);
        }
    }
}
