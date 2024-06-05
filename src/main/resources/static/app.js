const addPatientBtn = document.querySelector('#add-patient');
const selectedPatientsList = document.querySelector('#selected-patients');
const selectedPatientsTable = document.querySelector('#selected-patients-table tbody');
const selectPatients = document.querySelector('#selPatient');
const hIds = document.querySelector('#patientIds');
var idArray = [];

const removePatient = (patientId) => {
    console.log(idArray);
    const index = idArray.indexOf(patientId + '');
    console.log(patientId);
    console.log(index);
    if (index > -1) {
        document.querySelector('[rowId="'+patientId+'"]').remove();
        idArray.splice(index, 1);
    }
    
    hIds.value = idArray.join();
    console.log(hIds.value);
}

const addPatient = () => {
    if (selectPatients.selectedIndex == 0 || selectPatients.selectedIndex == -1 ) return;
    
    const patientId = selectPatients.value;
    if (idArray.includes(patientId)) return;

    var row = document.createElement('tr');
    row.setAttribute('rowID', patientId);
    selectedPatientsTable.appendChild(row);
    var cellTitle = document.createElement('td');
    cellTitle.textContent = selectPatients.options[selectPatients.selectedIndex].text;
    row.appendChild(cellTitle);
    var cellButton = document.createElement('td');
    cellButton.classList.add('text-end');
    cellButton.innerHTML = '<button type="button" class="btn btn-danger" onclick="removePatient('+patientId+')">-</button>';
    
    row.appendChild(cellButton);
    idArray.push(patientId);
    hIds.value = idArray.join();
    selectPatients.selectedIndex = 0;
}

addPatientBtn.addEventListener('click', addPatient);