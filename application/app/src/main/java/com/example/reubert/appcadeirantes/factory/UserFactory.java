package com.example.reubert.appcadeirantes.factory;

import com.example.reubert.appcadeirantes.model.User;

public class UserFactory {

    public static User create(
        String firstName, String lastName, String email,
        String password, String cpf, String birthday
    )
    {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setUsername(email);
        user.setPassword(password);
        user.setCPF(cpf);
        user.setBirthday(birthday);
        user.setPoints(0);
        user.setStatus(User.STATUS.Idle);
        return user;
    }

}
