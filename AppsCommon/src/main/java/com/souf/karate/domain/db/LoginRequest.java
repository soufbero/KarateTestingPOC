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
@Table(name = "LOGIN_REQUESTS")
public class LoginRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="SEQ_ID")
    private long sequenceID;

    @Column(name="TRAN_ID")
    private String tranId;

    @Column(name="USERNAME")
    private String userName;

    @Column(name="PASSWORD")
    private String password;

    @Column(name="LOGIN_INFO")
    private String loginInfo;

    @Column(name="TRAN_TS")
    private Instant tranTs;
}
