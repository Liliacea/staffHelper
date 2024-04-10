package ru.russiatex.commonjpa.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.russiatex.commonjpa.entity.BinaryContent;


public interface BinaryContentDAO extends JpaRepository<BinaryContent, Long> {
}
