package NULL.DTPomoziMi.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
@ToString(exclude = { "candidacies", "ratedBy", "ratedOthers", "authoredReqs", "executedReqs", "notifications" })
@Entity(name = "korisnik")
@Table(name = "korisnik")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_korisnik")
	private Long IdUser;

	@Column(name = "aktivan")
	private Boolean enabled;

	private String email;

	@Column(name = "ime")
	private String firstName;

	@Column(name = "lozinka")
	private String password;

	@Column(name = "prezime")
	private String lastName;

	private String token;

	@Column(name = "slika")
	private String picture;

	@ManyToOne
	@JoinColumn(name = "id_lokacija")
	private Location location;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
		name = "kandidiranje", joinColumns = { @JoinColumn(name = "id_korisnik") },
		inverseJoinColumns = { @JoinColumn(name = "id_kandidatura") }
	)
	private Set<Candidacy> candidacies = new HashSet<>();

	@OneToMany(mappedBy = "rated", cascade = CascadeType.ALL)
	private Set<Rating> ratedBy = new HashSet<>();
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private Set<Notification> notifications = new HashSet<>();

	@OneToMany(mappedBy = "rator", cascade = CascadeType.ALL)
	private Set<Rating> ratedOthers = new HashSet<>();

	@ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
	private Set<RoleEntity> roles = new HashSet<>();

	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
	private Set<Request> authoredReqs = new HashSet<>();

	@OneToMany(mappedBy = "executor", cascade = CascadeType.ALL)
	private Set<Request> executedReqs = new HashSet<>();

	public List<Role> getEnumRoles() { return roles.stream().map(r -> r.getRole()).collect(Collectors.toList()); }
	
	public Notification addNotification(Notification notification) {
		getNotifications().add(notification);
		notification.setUser(this);

		return notification;
	}

	public Notification removeNotification(Notification notification) {
		getNotifications().remove(notification);
		notification.setUser(null);

		return notification;
	}

	public Rating addRatedBy(Rating ratedBy) {
		getRatedBy().add(ratedBy);
		ratedBy.setRated(this);

		return ratedBy;
	}

	public Rating removeRatedBy(Rating ratedBy) {
		getRatedBy().remove(ratedBy);
		ratedBy.setRated(null);

		return ratedBy;
	}

	public Rating addRatedOther(Rating ratedOther) {
		getRatedOthers().add(ratedOther);
		ratedOther.setRator(this);

		return ratedOther;
	}

	public Rating removeRatedOther(Rating ratedOther) {
		getRatedOthers().remove(ratedOther);
		ratedOther.setRator(null);

		return ratedOther;
	}

	public Request addAuthoredReq(Request authoredReq) {
		getAuthoredReqs().add(authoredReq);
		authoredReq.setAuthor(this);

		return authoredReq;
	}

	public Request removeAuthoredReq(Request authoredReq) {
		getAuthoredReqs().remove(authoredReq);
		authoredReq.setAuthor(null);

		return authoredReq;
	}

	public Request addExecutedReq(Request executedReq) {
		getExecutedReqs().add(executedReq);
		executedReq.setExecutor(this);

		return executedReq;
	}

	public Request removeExecutedReq(Request executedReq) {
		getExecutedReqs().remove(executedReq);
		executedReq.setExecutor(null);

		return executedReq;
	}

	public RoleEntity addRoleEntity(RoleEntity roleEntity) {
		getRoles().add(roleEntity);
		roleEntity.getUsers().add(this);

		return roleEntity;
	}

	public RoleEntity removeRoleEntity(RoleEntity roleEntity) {
		getRoles().remove(roleEntity);
		roleEntity.getUsers().remove(null);

		return roleEntity;
	}

	public Candidacy addCandidacy(Candidacy candidacy) {
		getCandidacies().add(candidacy);
		candidacy.getUsers().add(this);

		return candidacy;
	}

	public Candidacy removeCandidacy(Candidacy candidacy) {
		getCandidacies().remove(candidacy);
		candidacy.getUsers().remove(null);

		return candidacy;
	}

}
