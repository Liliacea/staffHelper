package ru.russiatex.commonjpa.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.russiatex.commonjpa.entity.AppDocument;

public interface AppDocumentDAO extends JpaRepository<AppDocument, Long> {
}
