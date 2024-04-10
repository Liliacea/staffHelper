package ru.russiatex.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.GeneratorType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.persistence.*;

@Data
//@Getter
//@Setter
//@EqualsAndHashCode(exclude = "id")
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "raw_data")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class RawData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Update event;

}
