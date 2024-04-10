package ru.russiatex.commonjpa.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.russiatex.commonjpa.entity.AppPhoto;

public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> {
}
