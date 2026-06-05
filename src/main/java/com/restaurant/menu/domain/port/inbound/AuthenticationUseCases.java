package com.restaurant.menu.domain.port.inbound;

import com.restaurant.menu.domain.dto.LoginCommand;
import com.restaurant.menu.domain.dto.LoginResult;

public interface AuthenticationUseCases {
    LoginResult login(LoginCommand command);
}
