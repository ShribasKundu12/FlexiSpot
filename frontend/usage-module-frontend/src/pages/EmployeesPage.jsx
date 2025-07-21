// src/pages/EmployeesPage.jsx
import React, { useState, useEffect } from "react";
import DataTable from "../components/ui/DataTable";
import { format } from "date-fns";
import { analyticsApi } from "../services/api/analyticsApi";

const EmployeesPage = () => {
  const [inactiveEmployees, setInactiveEmployees] = useState([]);
  const [loading, setLoading] = useState(false);
  // The backend expects "since" as date; use today or a week ago as needed
  const since = format(new Date(), "yyyy-MM-dd");

  useEffect(() => {
    setLoading(true);
    analyticsApi.getInactiveEmployees(since)
      .then(res => {
        setInactiveEmployees(res.data.inactiveEmployees.map(name => ({ name })));
      })
      .finally(() => setLoading(false));
  }, [since]);

  const columns = [{ key: "name", label: "Employee Name" }];

  return (
    <div>
      <h2 className="text-xl font-bold mb-4">Inactive Employees</h2>
      {loading && <div>Loading...</div>}
      <DataTable data={inactiveEmployees} columns={columns} title="Inactive Employees" />
    </div>
  );
};

export default EmployeesPage;
