package NULL.DTPomoziMi.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
@ToString
@Entity(name = "ocjenjivanje")
@Table(name = "ocjenjivanje")
public class Rating implements Serializable {

	private static final long serialVersionUID = 1L;

	@Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_ocjenjivanje")
	private Long IdRating;

	@Column(name = "komentar")
	private String comment;

	@Column(name = "ocjena")
	private Integer rate;

	@ManyToOne
	@JoinColumn(name = "id_ocjenjeni")
	private User rated;

	@ManyToOne
	@JoinColumn(name = "id_ocjenjivac")
	private User rator;

	@ManyToOne
	@JoinColumn(name = "id_zahtjev")
	private Request request;

}
