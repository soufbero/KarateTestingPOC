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
@Table(name = "BAD_LOGINS")
public class BadLoginRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="SEQ_ID")
    private long sequenceID;

    @Column(name="TRAN_ID")
    private String tranId;

    @Column(name="REASON")
    private String reason;

    @Column(name="USERNAME")
    private String userName;

    @Column(name="TRAN_TS")
    private Instant tranTs;
}
