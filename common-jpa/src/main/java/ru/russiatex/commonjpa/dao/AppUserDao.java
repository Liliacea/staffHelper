package ru.russiatex.commonjpa.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.russiatex.commonjpa.entity.AppUser;

public interface AppUserDao extends JpaRepository<AppUser, Long> {
    AppUser findAppUserByTelegramUserId(Long id);
}
