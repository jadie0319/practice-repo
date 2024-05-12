package jadie.ticketorder.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("EI")
public class ExternalImage extends Image{
    protected ExternalImage() {
        super();
    }

    @Override
    public String getURL() {
        return null;
    }

    @Override
    public boolean hasThumbnail() {
        return false;
    }

    @Override
    public String getThumbnailURL() {
        return null;
    }
}
