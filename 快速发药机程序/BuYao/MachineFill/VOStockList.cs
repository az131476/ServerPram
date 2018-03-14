using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace MachineFill
{
    public class VOStockList
    {
        private string stockID;
        private string eqpID;
        private string rowNO;
        private string colNO;
        private string stockLength;
        private string stockWidth;
        private string stockHeight;
        private string stockHor;
        private string stockVer;
        private string state;
        private string quantity;
        private string checkQuantity;
        private string drugName;
        private string drugSpec;
        private string manufactory;
        private string packageLength;
        //新增盘点偏移量
        private string stock_Hor_correct;
        private string stock_Ver_correct;

        public string Stock_Hor_correct
        {
            get { return stock_Hor_correct; }
            set { stock_Hor_correct = value; }
        }
        public string Stock_Ver_correct 
        {
            get { return stock_Ver_correct; }
            set { stock_Ver_correct = value; }
        }
        public string StockID
        {
            get { return stockID; }
            set { stockID = value; }
        }

        public string EqpID
        {
            get { return eqpID; }
            set { eqpID = value; }
        }

        public string RowNO
        {
            get { return rowNO; }
            set { rowNO = value; }
        }

        public string ColNO
        {
            get { return colNO; }
            set { colNO = value; }
        }

        public string StockLength
        {
            get { return stockLength; }
            set { stockLength = value; }
        }

        public string StockWidth
        {
            get { return stockWidth; }
            set { stockWidth = value; }
        }

        public string StockHeight
        {
            get { return stockHeight; }
            set { stockHeight = value; }
        }

        public string StockHor
        {
            get { return stockHor; }
            set { stockHor = value; }
        }

        public string StockVer
        {
            get { return stockVer; }
            set { stockVer = value; }
        }

        public string State
        {
            get { return state; }
            set { state = value; }
        }

        public string Quantity
        {
            get { return quantity; }
            set { quantity = value; }
        }

        public string CheckQuantity
        {
            get { return checkQuantity; }
            set { checkQuantity = value; }
        }

        public string PackageLength
        {
            get { return packageLength; }
            set { packageLength = value; }
        }

        public string DrugName
        {
            get { return drugName; }
            set { drugName = value; }
        }

        public string DrugSpec
        {
            get { return drugSpec; }
            set { drugSpec = value; }
        }

        public string Manufactory
        {
            get { return manufactory; }
            set { manufactory = value; }
        }
    }
}
