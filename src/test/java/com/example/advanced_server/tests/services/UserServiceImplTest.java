package com.example.advanced_server.tests.services;

import com.example.advanced_server.dto.BaseSuccessResponse;
import com.example.advanced_server.dto.CustomSuccessResponse;
import com.example.advanced_server.dto.usersDto.PublicUserView;
import com.example.advanced_server.dto.usersDto.PutUserDto;
import com.example.advanced_server.dto.usersDto.PutUserDtoResponse;
import com.example.advanced_server.entity.UserEntity;
import com.example.advanced_server.exception.CustomException;
import com.example.advanced_server.exception.ValidationConstants;
import com.example.advanced_server.repository.UserRepository;
import com.example.advanced_server.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.example.advanced_server.tests.services.TestsConstants.incorrectPutUserDto;
import static com.example.advanced_server.tests.services.TestsConstants.putUserDto;
import static com.example.advanced_server.tests.services.TestsConstants.successStatusCode;
import static com.example.advanced_server.tests.services.TestsConstants.user;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    Authentication authentication;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private PublicUserView getFirstUserData(CustomSuccessResponse<List<PublicUserView>> response) {
        return response.getData().get(0);
    }

    @Test
    void successGetAllUserInfo() {

        when(userRepository.findAll()).thenReturn(List.of(user));

        CustomSuccessResponse<List<PublicUserView>> response = userService.getAllUserInfo();

        assertTrue(response.isSuccess());
        assertEquals(successStatusCode, response.getStatusCode());
        assertNotNull(getFirstUserData(response).getId());
        assertNotNull(getFirstUserData(response).getName());
        assertNotNull(getFirstUserData(response).getRole());
        assertNotNull(getFirstUserData(response).getEmail());
        assertNotNull(getFirstUserData(response).getAvatar());

        assertEquals(getFirstUserData(response).getName(), user.getName());
        assertEquals(getFirstUserData(response).getRole(), user.getRole());
        assertEquals(getFirstUserData(response).getEmail(), user.getEmail());
        assertEquals(getFirstUserData(response).getAvatar(), user.getAvatar());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void successGetInfoById() {

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        CustomSuccessResponse<PublicUserView> response = userService.getInfoById(user.getId());

        assertTrue(response.isSuccess());
        assertEquals(successStatusCode, response.getStatusCode());
        assertNotNull(response.getData().getId());
        assertNotNull(response.getData().getName());
        assertNotNull(response.getData().getRole());
        assertNotNull(response.getData().getEmail());
        assertNotNull(response.getData().getAvatar());

        assertEquals(response.getData().getName(), user.getName());
        assertEquals(response.getData().getRole(), user.getRole());
        assertEquals(response.getData().getEmail(), user.getEmail());
        assertEquals(response.getData().getAvatar(), user.getAvatar());

        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void shouldThrowException_WhenInvalidId() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        CustomException thrown = assertThrows(CustomException.class,
                () -> userService.getInfoById(UUID.randomUUID()));

        assertEquals(ValidationConstants.USER_NOT_FOUND, thrown.getMessage());
    }

    @Test
    void successGetUserInfo() {

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        CustomSuccessResponse<PublicUserView> response = userService.getUserInfo(user.getId());

        assertTrue(response.isSuccess());
        assertEquals(successStatusCode, response.getStatusCode());
        assertNotNull(response.getData().getAvatar());
        assertNotNull(response.getData().getId());
        assertNotNull(response.getData().getEmail());
        assertNotNull(response.getData().getName());
        assertNotNull(response.getData().getRole());

        assertEquals(response.getData().getName(), user.getName());
        assertEquals(response.getData().getRole(), user.getRole());
        assertEquals(response.getData().getEmail(), user.getEmail());
        assertEquals(response.getData().getAvatar(), user.getAvatar());

        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void successReplaceUser() throws IOException {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        FileServiceTest fileServiceTest = new FileServiceTest();
        fileServiceTest.successUploadFile();

        CustomSuccessResponse<PutUserDtoResponse> response = userService.replaceUser(user.getId(), putUserDto);

        assertTrue(response.isSuccess());
        assertEquals(successStatusCode, response.getStatusCode());
        assertNotNull(response.getData());

        assertEquals(response.getData().getName(), putUserDto.getName());
        assertEquals(response.getData().getRole(), putUserDto.getRole());
        assertEquals(response.getData().getEmail(), putUserDto.getEmail());
        assertEquals(response.getData().getAvatar(), putUserDto.getAvatar());

        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void testWhenInvalidEmail() {
        Set<ConstraintViolation<PutUserDto>> violations = validator.validate(incorrectPutUserDto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void successDeleteUser( ) {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        BaseSuccessResponse response = userService.deleteUser(user.getId());

        assertTrue(response.isSuccess());
        assertEquals(successStatusCode, response.getStatusCode());

        verify(userRepository, times(1)).deleteById(user.getId());
    }
}