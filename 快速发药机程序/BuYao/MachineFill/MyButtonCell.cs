using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Drawing;

namespace MachineFill
{
    public class MyButtonCell : DataGridViewButtonCell
    {
        protected override void Paint(Graphics graphics, Rectangle clipBounds, Rectangle cellBounds, int rowIndex, DataGridViewElementStates elementState, object value, object formattedValue, string errorText, DataGridViewCellStyle cellStyle, DataGridViewAdvancedBorderStyle advancedBorderStyle, DataGridViewPaintParts paintParts)
        {
            ButtonRenderer.DrawButton(graphics, cellBounds, formattedValue.ToString(), new Font("Comic Sans MS", 9.0f, FontStyle.Regular), true, System.Windows.Forms.VisualStyles.PushButtonState.Normal);
        }
    }
}
