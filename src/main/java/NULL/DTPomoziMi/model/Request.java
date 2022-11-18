package NULL.DTPomoziMi.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = { "ratings" })
@Table(name = "zahtjev")
@Entity(name = "zahtjev")
public class Request implements Serializable {

	private static final long serialVersionUID = 1L;

	@Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_zahtjev")
	private Long idRequest;

	@Column(name = "brojmobitela")
	private String phone;

	@Column(name = "opis")
	private String description;

	@Enumerated(EnumType.STRING)
	private RequestStatus status;

	private LocalDateTime tstmp;

	@OneToMany(mappedBy = "request", cascade = CascadeType.ALL)
	private Set<Rating> ratings = new HashSet<>();

	@ManyToOne
	@JoinColumn(name = "id_autor")
	private User author;

	@ManyToOne
	@JoinColumn(name = "id_izvrsitelj")
	private User executor;

	@ManyToOne
	@JoinColumn(name = "id_lokacija")
	private Location location;

	@Column(name = "exectstmp")
	private LocalDateTime execTstmp;
	
	@Column(name = "potvrden")
	private boolean confirmed;

	public Rating addRating(Rating rating) { getRatings().add(rating); rating.setRequest(this); return rating; }

	public Rating removeRating(Rating rating) {
		getRatings().remove(rating);
		rating.setRequest(null);

		return rating;
	}
}
