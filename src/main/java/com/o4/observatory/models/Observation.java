package com.o4.observatory.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "observations")
public class Observation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, length = 2000)
    private String description;
    
    // Astronomical coordinates
    @Column(nullable = false)
    private double rightAscension;

    @Column(nullable = false)
    private double declination;
    
    @ManyToOne
    @JoinColumn(name = "observatory_id")
    private Observatory observatory;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime modifiedAt;
    
    private String modificationReason;
    
    private int viewCount = 0;
    
    @OneToMany(mappedBy = "observation", cascade = CascadeType.ALL)
    private List<Comment> comments;
    
    @OneToMany(mappedBy = "observation", cascade = CascadeType.ALL)
    private List<Rating> ratings;
    
    @ManyToMany(mappedBy = "observations")
    private List<Collection> collections;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRightAscension() {
        return rightAscension;
    }

    public void setRightAscension(double rightAscension) {
        this.rightAscension = rightAscension;
    }

    public double getDeclination() {
        return declination;
    }

    public void setDeclination(double declination) {
        this.declination = declination;
    }

    public Observatory getObservatory() {
        return observatory;
    }

    public void setObservatory(Observatory observatory) {
        this.observatory = observatory;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getModificationReason() {
        return modificationReason;
    }

    public void setModificationReason(String modificationReason) {
        this.modificationReason = modificationReason;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public List<Collection> getCollections() {
        return collections;
    }

    public void setCollections(List<Collection> collections) {
        this.collections = collections;
    }

    @Override
    public String toString() {
        return "Observation{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", rightAscension=" + rightAscension +
            ", declination=" + declination +
            '}';
    }
    
}