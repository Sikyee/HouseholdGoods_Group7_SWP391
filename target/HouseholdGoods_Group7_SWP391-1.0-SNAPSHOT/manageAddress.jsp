<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.Address" %>
<%@ page import="Model.User" %>
<%
    Address address = (Address) request.getAttribute("editAddress");
    boolean isEdit = (address != null);
    String title = isEdit ? "Edit Address" : "Add New Address";

    // Get user from session to check address count
    User user = (User) session.getAttribute("user");
    String message = (String) session.getAttribute("message");
    String messageType = (String) session.getAttribute("messageType");

    // Clear session messages after displaying
    if (message != null) {
        session.removeAttribute("message");
        session.removeAttribute("messageType");
    }
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title><%= title%></title>
        <style>
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background-color: #f5f5f5;
                padding: 20px;
                line-height: 1.6;
            }

            .container {
                max-width: 800px;
                margin: 0 auto;
                background: white;
                border-radius: 10px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                padding: 30px;
            }

            .header {
                display: flex;
                align-items: center;
                margin-bottom: 30px;
                padding-bottom: 20px;
                border-bottom: 2px solid #e0e0e0;
            }

            .back-btn {
                background: #6c757d;
                color: white;
                border: none;
                padding: 10px 15px;
                border-radius: 5px;
                cursor: pointer;
                margin-right: 15px;
                text-decoration: none;
                display: inline-block;
                transition: background-color 0.3s;
            }

            .back-btn:hover {
                background: #5a6268;
            }

            h1 {
                color: #333;
                font-size: 24px;
            }

            .alert {
                padding: 15px;
                border-radius: 5px;
                margin-bottom: 20px;
                font-weight: 500;
            }

            .alert-success {
                background-color: #d4edda;
                color: #155724;
                border: 1px solid #c3e6cb;
            }

            .alert-error {
                background-color: #f8d7da;
                color: #721c24;
                border: 1px solid #f5c6cb;
            }

            .form-container {
                background: #fafafa;
                padding: 25px;
                border-radius: 8px;
                border: 1px solid #e0e0e0;
            }

            .form-row {
                display: flex;
                gap: 20px;
                margin-bottom: 20px;
            }

            .form-group {
                flex: 1;
            }

            .form-group.full-width {
                flex: 2;
            }

            label {
                display: block;
                margin-bottom: 5px;
                font-weight: 600;
                color: #555;
            }

            .required {
                color: #dc3545;
            }

            input[type="text"],
            input[type="tel"],
            select,
            textarea {
                width: 100%;
                padding: 12px;
                border: 2px solid #ddd;
                border-radius: 5px;
                font-size: 14px;
                transition: border-color 0.3s;
            }

            input[type="text"]:focus,
            input[type="tel"]:focus,
            select:focus,
            textarea:focus {
                outline: none;
                border-color: #007bff;
                box-shadow: 0 0 0 3px rgba(0,123,255,0.1);
            }

            textarea {
                min-height: 80px;
                resize: vertical;
            }

            .checkbox-group {
                display: flex;
                align-items: center;
                margin-bottom: 20px;
                padding: 15px;
                background: white;
                border-radius: 5px;
                border: 1px solid #ddd;
            }

            .checkbox-group input[type="checkbox"] {
                width: auto;
                margin-right: 10px;
                transform: scale(1.2);
            }

            .checkbox-group label {
                margin-bottom: 0;
                cursor: pointer;
            }

            .button-group {
                display: flex;
                gap: 10px;
                justify-content: flex-end;
                margin-top: 30px;
                padding-top: 20px;
                border-top: 1px solid #e0e0e0;
            }

            .btn {
                padding: 12px 25px;
                border: none;
                border-radius: 5px;
                cursor: pointer;
                font-size: 14px;
                font-weight: 600;
                text-decoration: none;
                display: inline-block;
                transition: all 0.3s;
            }

            .btn-primary {
                background-color: #007bff;
                color: white;
            }

            .btn-primary:hover {
                background-color: #0056b3;
                transform: translateY(-1px);
            }

            .btn-secondary {
                background-color: #6c757d;
                color: white;
            }

            .btn-secondary:hover {
                background-color: #5a6268;
            }

            .field-help {
                font-size: 12px;
                color: #666;
                margin-top: 5px;
            }

            .address-type-info {
                background: #e3f2fd;
                padding: 10px;
                border-radius: 5px;
                margin-bottom: 15px;
                font-size: 13px;
                color: #1565c0;
            }

            @media (max-width: 768px) {
                .container {
                    margin: 10px;
                    padding: 20px;
                }

                .form-row {
                    flex-direction: column;
                    gap: 0;
                }

                .button-group {
                    flex-direction: column;
                }
            }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="header">
                <a href="profile" class="back-btn">← Back to Profile</a>
                <h1><%= title%></h1>
            </div>

            <% if (message != null) {%>
            <div class="alert <%= "success".equals(messageType) ? "alert-success" : "alert-error"%>">
                <%= message%>
            </div>
            <% } %>

            <div class="form-container">
                <form method="post" action="manage-address" id="addressForm">
                    <% if (isEdit) {%>
                    <input type="hidden" name="addressID" value="<%= address.getAddressID()%>" />
                    <input type="hidden" name="action" value="update" />
                    <% } else { %>
                    <input type="hidden" name="action" value="add" />
                    <% } %>

                    <!-- Address Name and Type -->
                    <div class="form-row">
                        <div class="form-group">
                            <label for="addressName">Address Name <span class="required">*</span></label>
                            <input type="text" 
                                   id="addressName" 
                                   name="addressName" 
                                   required
                                   placeholder="e.g., Home, Office, etc."
                                   value="<%= isEdit && address.getAddressName() != null ? address.getAddressName() : ""%>" />
                            <div class="field-help">Give this address a memorable name</div>
                        </div>

                        <div class="form-group">
                            <label for="addressType">Address Type <span class="required">*</span></label>
                            <select id="addressType" name="addressType" required>
                                <option value="">Select Type</option>
                                <option value="HOME" <%= isEdit && "HOME".equals(address.getAddressType()) ? "selected" : ""%>>Home</option>
                                <option value="OFFICE" <%= isEdit && "OFFICE".equals(address.getAddressType()) ? "selected" : ""%>>Office</option>
                                <option value="OTHER" <%= isEdit && "OTHER".equals(address.getAddressType()) ? "selected" : ""%>>Other</option>
                            </select>
                        </div>
                    </div>

                    <!-- Recipient Information -->
                    <div class="form-row">
                        <div class="form-group">
                            <label for="recipientName">Full Name <span class="required">*</span></label>
                            <input type="text" 
                                   id="recipientName" 
                                   name="recipientName" 
                                   required
                                   placeholder="Full name of recipient"
                                   value="<%= isEdit && address.getRecipientName() != null ? address.getRecipientName() : ""%>" />
                        </div>

                        <div class="form-group">
                            <label for="phone">Phone Number <span class="required">*</span></label>
                            <input type="tel" 
                                   id="phone" 
                                   name="phone" 
                                   required
                                   placeholder="0123456789 or +84123456789"
                                   value="<%= isEdit && address.getPhone() != null ? address.getPhone() : ""%>" />
                            <div class="field-help">Vietnamese phone format (9-12 digits)</div>
                        </div>
                    </div>

                    <!-- Location Information -->
                    <div class="form-row">
                        <div class="form-group">
                            <label for="province">Province/City <span class="required">*</span></label>
                            <input type="text" 
                                   id="province" 
                                   name="province" 
                                   required
                                   placeholder="e.g., Ho Chi Minh City"
                                   value="<%= isEdit && address.getProvince() != null ? address.getProvince() : ""%>" />
                        </div>

                        <div class="form-group">
                            <label for="district">District <span class="required">*</span></label>
                            <input type="text" 
                                   id="district" 
                                   name="district" 
                                   required
                                   placeholder="e.g., District 1"
                                   value="<%= isEdit && address.getDistrict() != null ? address.getDistrict() : ""%>" />
                        </div>

                        <div class="form-group">
                            <label for="ward">Ward</label>
                            <input type="text" 
                                   id="ward" 
                                   name="ward" 
                                   placeholder="e.g., Ben Nghe Ward"
                                   value="<%= isEdit && address.getWard() != null ? address.getWard() : ""%>" />
                        </div>
                    </div>

                    <!-- Full Address Detail -->
                    <div class="form-row">
                        <div class="form-group full-width">
                            <label for="addressDetail">Complete Address <span class="required">*</span></label>
                            <textarea id="addressDetail" 
                                      name="addressDetail" 
                                      required
                                      placeholder="Enter the complete address details..."
                                      rows="3"><%= isEdit && address.getAddressDetail() != null ? address.getAddressDetail() : ""%></textarea>
                            <div class="field-help">This will be used for delivery. Please provide complete and accurate information.</div>
                        </div>
                    </div>

                    <!-- Default Address Checkbox -->
                    <div class="checkbox-group">
                        <input type="checkbox" 
                               id="isDefault" 
                               name="isDefault" 
                               value="1"
                               <%= isEdit && address.isDefault() ? "checked" : ""%> />
                        <label for="isDefault">Set as default address</label>
                    </div>

                    <div class="address-type-info">
                        <strong>Note:</strong> Your default address will be automatically selected for new orders. 
                        You can have only one default address at a time.
                    </div>

                    <div class="button-group">
                        <a href="profile" class="btn btn-secondary">Cancel</a>
                        <button type="submit" class="btn btn-primary">
                            <%= isEdit ? "Update Address" : "Add Address"%>
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <script>
            // Auto-fill complete address when individual fields are filled
            document.addEventListener('DOMContentLoaded', function () {
                const ward = document.getElementById('ward');
                const district = document.getElementById('district');
                const province = document.getElementById('province');
                const addressDetail = document.getElementById('addressDetail');

                function updateAddressDetail() {
                    const parts = [
                        ward.value,
                        district.value,
                        province.value
                    ].filter(part => part.trim() !== '');

                    if (parts.length > 0 && addressDetail.value.trim() === '') {
                        addressDetail.value = parts.join(', ');
                    }
                }

                [ward, district, province].forEach(field => {
                    if (field) {
                        field.addEventListener('blur', updateAddressDetail);
                    }
                });

                // Phone number validation
                const phoneInput = document.getElementById('phone');
                if (phoneInput) {
                    phoneInput.addEventListener('input', function () {
                        let phone = this.value.replace(/[^0-9+]/g, '');
                        
                        // Vietnamese phone validation
                        let isValid = false;
                        if (phone.startsWith('+84')) {
                            phone = phone.substring(3);
                            isValid = phone.length >= 9 && phone.length <= 10;
                        } else if (phone.startsWith('84')) {
                            phone = phone.substring(2);
                            isValid = phone.length >= 9 && phone.length <= 10;
                        } else if (phone.startsWith('0')) {
                            isValid = phone.length >= 10 && phone.length <= 11;
                        }

                        if (this.value && !isValid) {
                            this.style.borderColor = '#dc3545';
                        } else {
                            this.style.borderColor = '#ddd';
                        }
                    });
                }

                // Form validation
                const form = document.getElementById('addressForm');
                if (form) {
                    form.addEventListener('submit', function (e) {
                        const requiredFields = ['addressName', 'addressType', 'recipientName', 'phone', 'province', 'district', 'addressDetail'];
                        let isValid = true;
                        let firstInvalidField = null;

                        requiredFields.forEach(fieldName => {
                            const field = document.getElementById(fieldName);
                            if (field && !field.value.trim()) {
                                field.style.borderColor = '#dc3545';
                                isValid = false;
                                if (!firstInvalidField) {
                                    firstInvalidField = field;
                                }
                            } else if (field) {
                                field.style.borderColor = '#ddd';
                            }
                        });

                        // Validate phone number format
                        const phoneField = document.getElementById('phone');
                        if (phoneField && phoneField.value) {
                            let phone = phoneField.value.replace(/[^0-9+]/g, '');
                            let phoneValid = false;
                            
                            if (phone.startsWith('+84')) {
                                phone = phone.substring(3);
                                phoneValid = phone.length >= 9 && phone.length <= 10;
                            } else if (phone.startsWith('84')) {
                                phone = phone.substring(2);
                                phoneValid = phone.length >= 9 && phone.length <= 10;
                            } else if (phone.startsWith('0')) {
                                phoneValid = phone.length >= 10 && phone.length <= 11;
                            }
                            
                            if (!phoneValid) {
                                phoneField.style.borderColor = '#dc3545';
                                isValid = false;
                                if (!firstInvalidField) {
                                    firstInvalidField = phoneField;
                                }
                            }
                        }

                        if (!isValid) {
                            e.preventDefault();
                            alert('Please fill in all required fields correctly.');
                            if (firstInvalidField) {
                                firstInvalidField.focus();
                            }
                            return false;
                        }

                        // Show loading state
                        const submitBtn = form.querySelector('button[type="submit"]');
                        if (submitBtn) {
                            submitBtn.disabled = true;
                            submitBtn.textContent = 'Processing...';
                        }
                    });
                }
            });
        </script>
    </body>
</html>