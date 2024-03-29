package com.restaurant.smartfood.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "cancel_item_requests")
public class CancelItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_cancel_request_seq")
    @SequenceGenerator(name = "item_cancel_request_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime date;

    @ManyToOne//(cascade={CascadeType.MERGE})
    @JoinColumn
    private ItemInOrder itemInOrder;

    @ManyToOne(optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(nullable = false)
    private MenuItem menuItem;

    @ManyToOne//(cascade={CascadeType.MERGE})
    private OrderOfTable orderOfTable;

    private String reason;

    private Boolean isApproved=false;
}