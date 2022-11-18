package NULL.DTPomoziMi.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
@ToString(exclude = { "users" })
@Entity(name = "kandidatura")
@Table(name = "kandidatura")
public class Candidacy implements Serializable {
	private static final long serialVersionUID = 1L;

	@Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_kandidatura")
	private Long IdCandidacy;

	@Column(name = "godina")
	private Integer year;

	@ManyToOne
	@JoinColumn(name = "id_lokacija")
	private Location location;

	@ManyToMany(mappedBy = "candidacies")
	private Set<User> users = new HashSet<>();

}
