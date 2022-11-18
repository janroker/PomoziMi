package NULL.DTPomoziMi.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@ToString(exclude = { "candidacies", "users", "requests" })
@Entity(name = "lokacija")
@Table(name = "lokacija")
public class Location implements Serializable {

	private static final long serialVersionUID = 1L;

	@Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_lokacija")
	private Long IdLocation;

	@Column(name = "adresa")
	private String adress;

	@Column(name = "drzava")
	private String state;

	@Column(name = "duljina")
	private BigDecimal longitude;

	@Column(name = "naselje")
	private String town;

	@Column(name = "sirina")
	private BigDecimal latitude;

	@OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
	private Set<Candidacy> candidacies = new HashSet<>();

	@OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
	private Set<User> users = new HashSet<>();

	@OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
	private Set<Request> requests = new HashSet<>();

	public Candidacy addCandidacy(Candidacy candidacy) {
		getCandidacies().add(candidacy);
		candidacy.setLocation(this);

		return candidacy;
	}

	public Candidacy removeCandidacy(Candidacy candidacy) {
		getCandidacies().remove(candidacy);
		candidacy.setLocation(null);

		return candidacy;
	}

	public User addUser(User user) {
		getUsers().add(user);
		user.setLocation(this);

		return user;
	}

	public User removeUser(User user) {
		getUsers().remove(user);
		user.setLocation(null);

		return user;
	}

	public Request addRequest(Request request) {
		getRequests().add(request);
		request.setLocation(this);

		return request;
	}

	public Request removeRequest(Request request) {
		getRequests().remove(request);
		request.setLocation(null);

		return request;
	}
}
