package com.souf.karate.domain.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "EVENTS")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="SEQ_ID")
    private long sequenceID;

    @Column(name="TRAN_ID")
    private String tranId;

    @Column(name="EVT_ID")
    private int eventID;

    @Column(name="MESSAGE_TXT")
    private String message;

    @Column(name="TRAN_TS")
    private Instant tranTs;
}
