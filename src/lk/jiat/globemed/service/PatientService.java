package lk.jiat.globemed.service;

import java.util.List;
import lk.jiat.globemed.dao.PatientDao;
import lk.jiat.globemed.model.Patient;

public class PatientService {

    private final PatientDao patientDao;

    public PatientService() {
        this.patientDao = new PatientDao();
    }

    public Patient create(Patient patient) {
        return patientDao.create(patient);
    }

    public Patient update(Patient patient) {
        return patientDao.update(patient);
    }

    public boolean delete(Long id) {
        return patientDao.deleteById(id);
    }

    public Patient findById(Long id) {
        return patientDao.findById(id);
    }

    public List<Patient> findAll() {
        return patientDao.findAll();
    }

    /**
     * Find patients linked to a given doctor. This assumes Appointment has a
     * reference to both Patient and Doctor (Staff).
     */
    public List<Patient> findByDoctor(Long doctorId) {
        return patientDao.findByDoctor(doctorId);
    }
}
