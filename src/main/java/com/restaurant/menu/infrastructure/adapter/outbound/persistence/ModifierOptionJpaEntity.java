package com.restaurant.menu.infrastructure.adapter.outbound.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "modifier_options", schema = "menu")
public class ModifierOptionJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modifier_group_id", nullable = false)
    private ModifierGroupJpaEntity modifierGroup;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "price_adjustment", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAdjustment;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ModifierOptionJpaEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public ModifierGroupJpaEntity getModifierGroup() { return modifierGroup; }
    public void setModifierGroup(ModifierGroupJpaEntity modifierGroup) { this.modifierGroup = modifierGroup; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getPriceAdjustment() { return priceAdjustment; }
    public void setPriceAdjustment(BigDecimal priceAdjustment) { this.priceAdjustment = priceAdjustment; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
