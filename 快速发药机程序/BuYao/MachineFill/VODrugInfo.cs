using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace MachineFill
{
    public class VODrugInfo
    {
        private string drugCode;
        private string drugName;
        private string drugSpec;
        private string manufactory;
        private string drugBarCode;
        private string pyCode;
        private string packageLength;
        private string packageWidth;
        private string packageHeight;
        private string alarmStock;
        private string alarmStock2;
        private string outLimit;
        private string stockLimit;
        private string stockHoldquantity;
        private string storageLoc;

        public string DrugCode
        {
            get
            {
                return drugCode;
            }
            set
            {
                drugCode = value;
            }
        }

        public string DrugName
        {
            get
            {
                return drugName;
            }
            set
            {
                drugName = value;
            }
        }

        public string DrugSpec
        {
            get
            {
                return drugSpec;
            }
            set
            {
                drugSpec = value;
            }
        }

        public string Manufactory
        {
            get
            {
                return manufactory;
            }
            set
            {
                manufactory = value;
            }
        }

        public string DrugBarCode
        {
            get
            {
                return drugBarCode;
            }
            set
            {
                drugBarCode = value;
            }
        }

        public string PyCode
        {
            get
            {
                return pyCode;
            }
            set
            {
                pyCode = value;
            }
        }

        public string PackageLength
        {
            get
            {
                return packageLength;
            }
            set
            {
                packageLength = value;
            }
        }

        public string PackageWidth
        {
            get
            {
                return packageWidth;
            }
            set
            {
                packageWidth = value;
            }
        }

        public string PackageHeight
        {
            get
            {
                return packageHeight;
            }
            set
            {
                packageHeight = value;
            }
        }

        public string AlarmStock
        {
            get
            {
                return alarmStock;
            }
            set
            {
                alarmStock = value;
            }
        }

        public string AlarmStock2
        {
            get
            {
                return alarmStock2;
            }
            set
            {
                alarmStock2 = value;
            }
        }

        public string StockLimit
        {
            get
            {
                return stockLimit;
            }
            set
            {
                stockLimit = value;
            }
        }

        public string StockHoldquantity
        {
            get
            {
                return stockHoldquantity;
            }
            set
            {
                stockHoldquantity = value;
            }
        }

        public string OutLimit
        {
            get
            {
                return outLimit;
            }
            set
            {
                outLimit = value;
            }
        }

        public string StorageLoc
        {
            get
            {
                return storageLoc;
            }
            set
            {
                storageLoc = value;
            }
        }
    }
}
