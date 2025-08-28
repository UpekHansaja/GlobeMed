package lk.jiat.globemed.mediator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

/**
 * Concrete Mediator for Admin Dashboard
 * Coordinates interactions between admin dashboard components
 */
public class AdminDashboardMediator implements DashboardMediator {
    
    // Component registry
    private Map<String, Component> components;
    
    // UI Components
    private JButton addUserBtn;
    private JButton editUserBtn;
    private JButton deleteUserBtn;
    private JButton refreshBtn;
    private JTable userTable;
    private JTextField searchField;
    private JComboBox<String> roleFilter;
    private JLabel statusLabel;
    
    public AdminDashboardMediator() {
        this.components = new HashMap<>();
    }
    
    // Initialize with components
    public AdminDashboardMediator(JButton addBtn, JButton editBtn, JButton deleteBtn, 
                                 JButton refreshBtn, JTable table, JTextField search, 
                                 JComboBox<String> roleFilter, JLabel statusLabel) {
        this();
        this.addUserBtn = addBtn;
        this.editUserBtn = editBtn;
        this.deleteUserBtn = deleteBtn;
        this.refreshBtn = refreshBtn;
        this.userTable = table;
        this.searchField = search;
        this.roleFilter = roleFilter;
        this.statusLabel = statusLabel;
        
        // Register components
        registerComponent("addButton", addBtn);
        registerComponent("editButton", editBtn);
        registerComponent("deleteButton", deleteBtn);
        registerComponent("refreshButton", refreshBtn);
        registerComponent("userTable", table);
        registerComponent("searchField", search);
        registerComponent("roleFilter", roleFilter);
        registerComponent("statusLabel", statusLabel);
        
        // Initialize component states
        initializeComponentStates();
    }
    
    @Override
    public void notify(Component sender, String event, Object data) {
        System.out.println(String.format("🔄 Mediator received: %s from %s", event, getComponentName(sender)));
        
        if (sender == userTable) {
            handleTableEvents(event, data);
        } else if (sender == searchField) {
            handleSearchEvents(event, data);
        } else if (sender == roleFilter) {
            handleFilterEvents(event, data);
        } else if (sender == addUserBtn) {
            handleAddUserEvents(event, data);
        } else if (sender == editUserBtn) {
            handleEditUserEvents(event, data);
        } else if (sender == deleteUserBtn) {
            handleDeleteUserEvents(event, data);
        } else if (sender == refreshBtn) {
            handleRefreshEvents(event, data);
        }
    }
    
    private void handleTableEvents(String event, Object data) {
        switch (event) {
            case "SELECTION_CHANGED":
                boolean hasSelection = userTable.getSelectedRow() != -1;
                editUserBtn.setEnabled(hasSelection);
                deleteUserBtn.setEnabled(hasSelection);
                
                if (hasSelection) {
                    int selectedRow = userTable.getSelectedRow();
                    String userName = (String) userTable.getValueAt(selectedRow, 1); // Assuming name is in column 1
                    updateStatus("Selected user: " + userName);
                } else {
                    updateStatus("No user selected");
                }
                break;
                
            case "DOUBLE_CLICK":
                if (userTable.getSelectedRow() != -1) {
                    // Trigger edit action
                    handleEditUserEvents("EDIT_USER", null);
                }
                break;
        }
    }
    
    private void handleSearchEvents(String event, Object data) {
        switch (event) {
            case "TEXT_CHANGED":
                String searchText = searchField.getText();
                filterTable(searchText, (String) roleFilter.getSelectedItem());
                updateStatus("Searching for: " + (searchText.isEmpty() ? "all users" : searchText));
                break;
                
            case "ENTER_PRESSED":
                // Perform advanced search or focus first result
                if (userTable.getRowCount() > 0) {
                    userTable.setRowSelectionInterval(0, 0);
                    notify(userTable, "SELECTION_CHANGED", null);
                }
                break;
        }
    }
    
    private void handleFilterEvents(String event, Object data) {
        switch (event) {
            case "SELECTION_CHANGED":
                String selectedRole = (String) roleFilter.getSelectedItem();
                filterTable(searchField.getText(), selectedRole);
                updateStatus("Filtered by role: " + (selectedRole == null ? "All" : selectedRole));
                break;
        }
    }
    
    private void handleAddUserEvents(String event, Object data) {
        switch (event) {
            case "ADD_USER":
                // Clear selection and prepare for new user
                userTable.clearSelection();
                editUserBtn.setEnabled(false);
                deleteUserBtn.setEnabled(false);
                updateStatus("Adding new user...");
                
                // In real implementation, would open add user dialog
                showAddUserDialog();
                break;
        }
    }
    
    private void handleEditUserEvents(String event, Object data) {
        switch (event) {
            case "EDIT_USER":
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow != -1) {
                    String userName = (String) userTable.getValueAt(selectedRow, 1);
                    updateStatus("Editing user: " + userName);
                    
                    // In real implementation, would open edit user dialog
                    showEditUserDialog(selectedRow);
                }
                break;
        }
    }
    
    private void handleDeleteUserEvents(String event, Object data) {
        switch (event) {
            case "DELETE_USER":
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow != -1) {
                    String userName = (String) userTable.getValueAt(selectedRow, 1);
                    
                    // Show confirmation dialog
                    int result = JOptionPane.showConfirmDialog(
                        userTable.getParent(),
                        "Are you sure you want to delete user: " + userName + "?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );
                    
                    if (result == JOptionPane.YES_OPTION) {
                        // Remove from table
                        DefaultTableModel model = (DefaultTableModel) userTable.getModel();
                        model.removeRow(selectedRow);
                        
                        updateStatus("Deleted user: " + userName);
                        
                        // Update button states
                        editUserBtn.setEnabled(false);
                        deleteUserBtn.setEnabled(false);
                    }
                }
                break;
        }
    }
    
    private void handleRefreshEvents(String event, Object data) {
        switch (event) {
            case "REFRESH_DATA":
                // Clear search and filters
                searchField.setText("");
                roleFilter.setSelectedIndex(0);
                userTable.clearSelection();
                
                // Refresh table data
                refreshTableData();
                
                updateStatus("Data refreshed");
                
                // Update button states
                editUserBtn.setEnabled(false);
                deleteUserBtn.setEnabled(false);
                break;
        }
    }
    
    private void filterTable(String searchText, String roleFilter) {
        // In real implementation, would filter the table model
        System.out.println("🔍 Filtering table: search='" + searchText + "', role='" + roleFilter + "'");
        
        // Simulate filtering by updating row count display
        int visibleRows = calculateVisibleRows(searchText, roleFilter);
        updateStatus(String.format("Showing %d users", visibleRows));
    }
    
    private int calculateVisibleRows(String searchText, String roleFilter) {
        // Simulate filtering logic
        int totalRows = userTable.getRowCount();
        if (searchText != null && !searchText.isEmpty()) {
            totalRows = Math.max(1, totalRows - 2); // Simulate search filtering
        }
        if (roleFilter != null && !"All".equals(roleFilter)) {
            totalRows = Math.max(1, totalRows - 1); // Simulate role filtering
        }
        return totalRows;
    }
    
    private void showAddUserDialog() {
        // Simulate dialog
        System.out.println("📝 Opening Add User dialog...");
        JOptionPane.showMessageDialog(userTable.getParent(), 
            "Add User dialog would open here", 
            "Add User", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showEditUserDialog(int selectedRow) {
        // Simulate dialog
        String userName = (String) userTable.getValueAt(selectedRow, 1);
        System.out.println("✏️ Opening Edit User dialog for: " + userName);
        JOptionPane.showMessageDialog(userTable.getParent(), 
            "Edit User dialog for " + userName + " would open here", 
            "Edit User", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void refreshTableData() {
        // Simulate data refresh
        System.out.println("🔄 Refreshing table data...");
        
        // In real implementation, would reload data from database
        DefaultTableModel model = (DefaultTableModel) userTable.getModel();
        // model.setRowCount(0);
        // loadUsersFromDatabase();
    }
    
    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
        System.out.println("📊 Status: " + message);
    }
    
    private void initializeComponentStates() {
        // Set initial states
        editUserBtn.setEnabled(false);
        deleteUserBtn.setEnabled(false);
        updateStatus("Ready");
    }
    
    private String getComponentName(Component component) {
        for (Map.Entry<String, Component> entry : components.entrySet()) {
            if (entry.getValue() == component) {
                return entry.getKey();
            }
        }
        return "unknown";
    }
    
    @Override
    public void registerComponent(String name, Component component) {
        components.put(name, component);
        System.out.println("📋 Registered component: " + name);
    }
    
    @Override
    public void unregisterComponent(String name) {
        components.remove(name);
        System.out.println("📋 Unregistered component: " + name);
    }
    
    // Utility methods for external access
    public void simulateUserSelection(int row) {
        if (userTable != null && row >= 0 && row < userTable.getRowCount()) {
            userTable.setRowSelectionInterval(row, row);
            notify(userTable, "SELECTION_CHANGED", null);
        }
    }
    
    public void simulateSearch(String text) {
        if (searchField != null) {
            searchField.setText(text);
            notify(searchField, "TEXT_CHANGED", null);
        }
    }
    
    public void simulateRoleFilter(String role) {
        if (roleFilter != null) {
            roleFilter.setSelectedItem(role);
            notify(roleFilter, "SELECTION_CHANGED", null);
        }
    }
}