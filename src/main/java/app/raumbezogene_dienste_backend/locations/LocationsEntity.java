package app.raumbezogene_dienste_backend.locations;

import jakarta.persistence.Entity;
import jakarta.persistence.*;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

@Entity
@Table(name = "locations")
public class LocationsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "geo_data")
    private String geo_data;  // GEOMETRY type

    public LocationsEntity() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGeo_data() {
        return geo_data;
    }

    public void setGeo_data(String geo_data) {
        this.geo_data = geo_data;
    }

    @Override
    public String toString() {
        return "LocationsEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", geo_data='" + geo_data + '\'' +
                '}';
    }

    public static RowMapper<LocationsEntity> getRowMapper() {
        return new RowMapper<LocationsEntity>() {
            @Override
            public LocationsEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
                LocationsEntity location = new LocationsEntity();
                location.setId(rs.getLong("id"));
                location.setTitle(rs.getString("title"));  // Changed from getName
                location.setDescription(rs.getString("description"));
                location.setGeo_data(rs.getString("geo_data"));

                return location;
            }
        };
    }


}
