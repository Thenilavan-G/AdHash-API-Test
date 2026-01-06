#!/usr/bin/env python3
"""
Email notification script for AdHash API Tests.
Sends card-based HTML emails with test results.
"""
import smtplib
import re
import os
import sys
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from email.mime.base import MIMEBase
from email import encoders


def extract_ssl_issues(report_path):
    """Extract SSL certificate issues from HTML report."""
    ssl_issues = []
    if report_path and os.path.exists(report_path):
        with open(report_path, 'r', encoding='utf-8') as f:
            content = f.read()
            ssl_match = re.search(r'SSL Certificate Issues Detected.*?<ul.*?>(.*?)</ul>', content, re.DOTALL)
            if ssl_match:
                items = re.findall(r'<li.*?><strong>(.*?)</strong>.*?<a href="(.*?)"', ssl_match.group(1), re.DOTALL)
                ssl_issues = [(name.strip(), url.strip()) for name, url in items]
    return ssl_issues


def build_ssl_html(ssl_issues):
    """Build SSL warning section HTML."""
    if not ssl_issues:
        return ""
    
    ssl_items = ""
    for name, url in ssl_issues:
        ssl_items += f'<div style="padding:8px 0;border-bottom:1px solid #FFE0B2;"><strong style="color:#E65100;">⚠ {name}</strong><br><a href="{url}" style="color:#FF6D00;font-size:12px;word-break:break-all;">{url}</a></div>'
    
    return f'''
    <div style="background:#FFF3E0;border-radius:8px;padding:15px;margin-top:15px;border-left:4px solid #FF9800;">
      <div style="font-weight:bold;color:#E65100;margin-bottom:10px;">🔒 SSL CERTIFICATE WARNING - {len(ssl_issues)} Issue(s)</div>
      {ssl_items}
    </div>'''


def build_html_body(status, timestamp, tests_run, passed, failed, success_rate, ssl_html):
    """Build the card-based HTML email body."""
    is_success = status == 'SUCCESS'
    
    # Color scheme based on status
    header_gradient = 'linear-gradient(135deg,#4CAF50,#2E7D32)' if is_success else 'linear-gradient(135deg,#F44336,#C62828)'
    status_bg = '#4CAF50' if is_success else '#F44336'
    status_text = '✓ ALL TESTS PASSED' if is_success else '✗ TESTS FAILED'
    rate_color = '#4CAF50' if is_success else '#F44336'
    attach_bg = '#E8F5E9' if is_success else '#FFEBEE'
    attach_border = '#4CAF50' if is_success else '#F44336'
    attach_title_color = '#2E7D32' if is_success else '#C62828'
    attach_text_color = '#558B2F' if is_success else '#D32F2F'
    attach_note = 'Open the HTML attachment for complete test details.' if is_success else 'Open the HTML attachment for complete test details and error information.'
    
    return f'''<!DOCTYPE html>
<html><head><meta charset="UTF-8"></head>
<body style="margin:0;padding:20px;background:#f5f5f5;font-family:Arial,sans-serif;">
<table width="600" align="center" cellpadding="0" cellspacing="0" style="background:#fff;border-radius:12px;box-shadow:0 2px 8px rgba(0,0,0,0.1);">

<!-- Header -->
<tr><td style="background:{header_gradient};padding:25px;text-align:center;border-radius:12px 12px 0 0;">
<h1 style="margin:0;color:#fff;font-size:22px;">🎯 AdHash API Test Report</h1>
<p style="margin:8px 0 0;color:rgba(255,255,255,0.9);font-size:13px;">{timestamp}</p>
</td></tr>

<!-- Stats Cards -->
<tr><td style="padding:20px;">
<table width="100%" cellpadding="0" cellspacing="8">
<tr>
<td width="33%" align="center" style="background:#E3F2FD;padding:15px;border-radius:8px;">
<div style="font-size:28px;font-weight:bold;color:#1976D2;">{tests_run}</div>
<div style="font-size:11px;color:#666;margin-top:4px;">Total Tests</div></td>
<td width="33%" align="center" style="background:#E8F5E9;padding:15px;border-radius:8px;">
<div style="font-size:28px;font-weight:bold;color:#388E3C;">✓ {passed}</div>
<div style="font-size:11px;color:#666;margin-top:4px;">Passed</div></td>
<td width="33%" align="center" style="background:#FFEBEE;padding:15px;border-radius:8px;">
<div style="font-size:28px;font-weight:bold;color:#D32F2F;">✗ {failed}</div>
<div style="font-size:11px;color:#666;margin-top:4px;">Failed</div></td>
</tr></table>

<!-- Success Rate & Status -->
<div style="text-align:center;margin:20px 0;">
<div style="font-size:42px;font-weight:bold;color:{rate_color};">{success_rate}%</div>
<div style="font-size:12px;color:#666;">Success Rate</div>
</div>

<div style="background:{status_bg};color:#fff;text-align:center;padding:12px;border-radius:8px;font-weight:bold;font-size:14px;">
{status_text}
</div>

<!-- SSL Warning (if any) -->
{ssl_html}

<!-- Attachment Note -->
<div style="background:{attach_bg};border-radius:8px;padding:12px;margin-top:15px;border-left:4px solid {attach_border};">
<strong style="color:{attach_title_color};">📎 Detailed report attached</strong>
<div style="font-size:12px;color:{attach_text_color};margin-top:4px;">{attach_note}</div>
</div>

</td></tr>

<!-- Footer -->
<tr><td style="background:#FAFAFA;padding:15px;text-align:center;border-radius:0 0 12px 12px;border-top:1px solid #EEE;">
<p style="margin:0;font-size:11px;color:#999;">AdHash API Test Automation | {timestamp}</p>
</td></tr>

</table></body></html>'''


def send_email(status, timestamp, tests_run, passed, failed, success_rate, report_path,
               smtp_server, smtp_port, username, password, from_email, to_email):
    """Send the email notification."""
    # Extract SSL issues
    ssl_issues = extract_ssl_issues(report_path)
    ssl_html = build_ssl_html(ssl_issues)
    
    # Build HTML body
    html_body = build_html_body(status, timestamp, tests_run, passed, failed, success_rate, ssl_html)
    
    # Subject based on status
    emoji = '✅' if status == 'SUCCESS' else '❌'
    result = 'SUCCESS' if status == 'SUCCESS' else 'FAILED'
    subject = f"{emoji} AdHash Daily API Tests - {result} ({timestamp})"
    
    # Create message
    msg = MIMEMultipart()
    msg['From'] = from_email
    msg['To'] = to_email
    msg['Subject'] = subject
    msg.attach(MIMEText(html_body, 'html'))
    
    # Attach HTML report if available
    if report_path and os.path.exists(report_path):
        with open(report_path, 'rb') as f:
            part = MIMEBase('application', 'octet-stream')
            part.set_payload(f.read())
            encoders.encode_base64(part)
            part.add_header('Content-Disposition', f'attachment; filename="{os.path.basename(report_path)}"')
            msg.attach(part)
    
    # Send email
    with smtplib.SMTP_SSL(smtp_server, smtp_port) as server:
        server.login(username, password)
        server.send_message(msg)
    
    print(f"{result} email sent successfully!")


if __name__ == '__main__':
    # Get parameters from environment variables
    send_email(
        status=os.environ.get('TEST_STATUS', 'FAILURE'),
        timestamp=os.environ.get('TEST_TIMESTAMP', ''),
        tests_run=os.environ.get('TESTS_RUN', '0'),
        passed=os.environ.get('TESTS_PASSED', '0'),
        failed=os.environ.get('TESTS_FAILED', '0'),
        success_rate=os.environ.get('SUCCESS_RATE', '0'),
        report_path=os.environ.get('REPORT_PATH', ''),
        smtp_server='smtp.zoho.com',
        smtp_port=465,
        username=os.environ.get('EMAIL_USERNAME', ''),
        password=os.environ.get('EMAIL_PASSWORD', ''),
        from_email=os.environ.get('EMAIL_USERNAME', ''),
        to_email=os.environ.get('EMAIL_TO', '')
    )

