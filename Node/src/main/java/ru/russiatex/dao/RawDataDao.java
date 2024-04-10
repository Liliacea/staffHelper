package ru.russiatex.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.russiatex.entity.RawData;

public interface RawDataDao extends JpaRepository<RawData,Long> {
}
