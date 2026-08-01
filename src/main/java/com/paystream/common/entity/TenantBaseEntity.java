package com.paystream.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public abstract class TenantBaseEntity extends BaseEntity<UUID> {

    @Id
    @Column(name = "id", nullable = false)
    private String id = UUID.randomUUID().toString();

    @Column(name = "merchant_id", length = 36)
    private String merchantId;

    @Column(name = "po_number", length = 100)
    private String poNumber;
}
