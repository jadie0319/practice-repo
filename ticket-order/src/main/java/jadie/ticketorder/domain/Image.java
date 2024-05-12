package jadie.ticketorder.domain;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "image_type")
@Table(name = "image")
public abstract class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;
    protected Image() {}

    public abstract String getURL();
    public abstract boolean hasThumbnail();
    public abstract String getThumbnailURL();
}
