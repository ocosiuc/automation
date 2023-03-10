package framework;

import java.util.Properties;

/**
 * Class to encapsulate the datatable related functions of the framework
 * 
 * @author
 */
public class DataTable {
	private final String datatablePath, datatableName;
	@SuppressWarnings("unused")
	private String dataReferenceIdentifier = "#";

	private String currentTestcase;
	private int currentIteration = 0, currentSubIteration = 0;
	private static Properties properties;

	/**
	 * Constructor to initialize the {@link DataTable} object
	 * 
	 * @param datatablePath The path where the datatable is stored
	 * @param datatableName The name of the datatable file
	 */
	public DataTable(String datatablePath, String datatableName) {
		this.datatablePath = datatablePath;
		this.datatableName = datatableName;
		properties = Settings.getInstance();
	}

	/**
	 * Function to set the data reference identifier character
	 * 
	 * @param dataReferenceIdentifier The data reference identifier character
	 */
	public void setDataReferenceIdentifier(String dataReferenceIdentifier) {
		if (dataReferenceIdentifier.length() != 1) {
			System.out.println("The data reference identifier must be a single character!");
		}

		this.dataReferenceIdentifier = dataReferenceIdentifier;
	}

	/**
	 * Function to set the variables required to uniquely identify the exact row of
	 * data under consideration
	 * 
	 * @param currentTestcase     The ID of the current test case
	 * @param currentIteration    The Iteration being executed currently
	 * @param currentSubIteration The Sub-Iteration being executed currently
	 */
	public void setCurrentRow(String currentTestcase, int currentIteration, int currentSubIteration) {
		this.currentTestcase = currentTestcase;
		this.currentIteration = currentIteration;
		this.currentSubIteration = currentSubIteration;
	}

	private void checkPreRequisites() {
		if (currentTestcase == null) {
			System.out.println("DataTable.currentTestCase is not set!");
		}
	}

	/**
	 * Function to return the test data value corresponding to the sheet name and
	 * field name passed
	 * 
	 * @param datasheetName The name of the sheet in which the data is present
	 * @param fieldName     The name of the field whose value is required
	 * @return The test data present in the field name specified
	 * @see #putData(String, String, String)
	 * @see #getExpectedResult(String)
	 */
	public String getData(String datasheetName, String fieldName) {
		checkPreRequisites();

		ExcelDataAccess testDataAccess = new ExcelDataAccess(datatablePath, datatableName);
		testDataAccess.setDatasheetName(datasheetName);

		int rowNum = testDataAccess.getRowNum(currentTestcase, 0, 1); 
		if (rowNum == -1) {
			System.out.println("The test case \"" + currentTestcase + "\"" + "is not found in the test data sheet \""
					+ datasheetName + "\"!");
		}

		String dataValue = testDataAccess.getValue(rowNum, fieldName);


		return dataValue;
	}


	

	/**
	 * Function to get the expected result corresponding to the field name passed
	 * 
	 * @param fieldName The name of the field which contains the expected results
	 * @return The expected result present in the field name specified
	 * @see #getData(String, String)
	 */
	public String getExpectedResult(String fieldName) {
		checkPreRequisites();

		ExcelDataAccess expectedResultsAccess = new ExcelDataAccess(datatablePath, datatableName);
		expectedResultsAccess.setDatasheetName("Parametrized_Checkpoints");

		int rowNum = expectedResultsAccess.getRowNum(currentTestcase, 0, 1);
		if (rowNum == -1) {
			System.out.println("The test case \"" + currentTestcase + "\""
					+ "is not found in the parametrized checkpoints sheet!");
		}
		rowNum = expectedResultsAccess.getRowNum(Integer.toString(currentIteration), 1, rowNum);
		if (rowNum == -1) {
			System.out.println("The iteration number \"" + currentIteration + "\"" + "of the test case \""
					+ currentTestcase + "\"" + "is not found in the parametrized checkpoints sheet!");
		}
		if (properties.getProperty("Approach").equalsIgnoreCase("KeywordDriven")) {
			rowNum = expectedResultsAccess.getRowNum(Integer.toString(currentSubIteration), 2, rowNum);
			if (rowNum == -1) {
				System.out.println("The sub iteration number \"" + currentSubIteration + "\""
						+ "under iteration number \"" + currentIteration + "\"" + "of the test case \""
						+ currentTestcase + "\"" + "is not found in the parametrized checkpoints sheet!");
			}
		}

		return expectedResultsAccess.getValue(rowNum, fieldName);
	}

	public String getPath() {
		return this.datatablePath;
	}
}