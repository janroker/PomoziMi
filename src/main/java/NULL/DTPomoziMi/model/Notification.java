package NULL.DTPomoziMi.model;

import java.io.Serializable;
import java.time.LocalDateTime;

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
@Table(name = "obavijest")
@Entity(name = "obavijest")
public class Notification implements Serializable {
	private static final long serialVersionUID = 1L;

	@Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_obavijest")
	private Long idNotification;
	
	@Column(name = "obavijest")
	private String message;
	
	@Column(name = "primljena")
	private Boolean received;
	
	@ManyToOne()
	@JoinColumn(name = "id_korisnik")
	private User user;
	
	private LocalDateTime tstmp;
	
}
