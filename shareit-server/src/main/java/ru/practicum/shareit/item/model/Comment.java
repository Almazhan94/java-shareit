package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.error.Generated;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Generated
@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String text;

    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    Item item;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    User author;

    @CreationTimestamp
    LocalDateTime created;
}
